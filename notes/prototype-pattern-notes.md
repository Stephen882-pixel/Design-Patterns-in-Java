# The Prototype Design Pattern in Java

Notes for the "Design Patterns in Java" session (Saturday, 10 October 2026).
Each `##` heading below is meant to become one slide.

Code lives in:
- `src/main/java/com/example/PrototypeDesignPattern/Prototype.java`: the "I can copy myself" interface
- `src/main/java/com/example/PrototypeDesignPattern/GameBotCharacters.java`: the object being cloned
- `src/main/java/com/example/PrototypeDesignPattern/BotRegistry.java`: a shelf of ready-made templates
- `src/main/java/com/example/Main.java`: the client (`prototypeDemo()`)

---

## 1. What problem are we solving?

Some objects are **expensive to create**.

Imagine a game that needs 50 enemy bots. Every time you write `new GameBotCharacters(...)`, the constructor:

```
Loading Character animations from DB.....
Loading sound effects from DB.....
Preparing AI Battle settings file.....
```

That takes **2 seconds per bot** in our demo. 50 bots means 100 seconds of loading, and the bots are **almost identical**. Only the name, health and attack power change.

The waste: we keep doing the same heavy setup to get things we already have.

---

## 2. The idea in one sentence

> **Build one object the hard way, then make new objects by copying it and changing only what's different.**

The object you copy from is called the **prototype**.

---

## 3. Real-life analogies

| Situation | The prototype | The "clone + tweak" |
|---|---|---|
| **Writing a CV** | A CV template with your layout, fonts and sections | Copy it, change the job title and summary for each application |
| **Office letters / memos** | The company letterhead with logo, address and signature block | Copy it, write the new body and date |
| **Photocopier** | The original document | Photocopy it, write a different name on each copy |
| **Baking with a cookie cutter** | The cutter shape (made once, carefully) | Stamp out cookies, decorate each one differently |
| **Biology** | Dolly the sheep's original cell | A clone. Same DNA, separate sheep |

**Ask the audience:** "Who here starts a new Word document from a blank page, and who copies last month's report and edits it?" Most people already use the Prototype pattern.

---

## 4. The players

| Role | Our class | Layman's job |
|---|---|---|
| **Prototype** (interface) | `Prototype<T>` | The promise: "I know how to copy myself" |
| **Concrete Prototype** | `GameBotCharacters` | The real object that does the copying |
| **Client** | `Main.prototypeDemo()` | Asks for copies instead of calling `new` |
| **Registry** (optional) | `BotRegistry` | A shelf of named templates to copy from |

---

## 5. The interface

```java
public interface Prototype<T> {
    T customizeClone();
}
```

- Any class that implements this is saying: **"you can ask me for a copy of myself."**
- The client doesn't need to know *how* the copy is made. It just calls `customizeClone()`.

**Why not call it `Cloneable`?** Java already has a built-in `java.lang.Cloneable`. Giving ours the same name is confusing, and inside the package it hides the built-in one. (See slide 12 for why we don't use the built-in one.)

---

## 6. How the copy is made: the copy constructor

```java
// The expensive one: used ONCE
public GameBotCharacters(String name, int health, int attackPower, List<String> weapons) {
    System.out.println("Loading Character animations from DB.....");
    ...
    Thread.sleep(LOADING_TIME_MS);
    ...
}

// The cheap one: used for every clone
private GameBotCharacters(GameBotCharacters gbc) {
    this.name = gbc.name;
    this.health = gbc.health;
    this.attackPower = gbc.attackPower;
    this.weapons = new ArrayList<>(gbc.weapons);
}

@Override
public GameBotCharacters customizeClone() {
    return new GameBotCharacters(this);
}
```

- The copy constructor **just copies fields**. No DB, no loading, no waiting.
- It's `private`, so the only way to use it from outside is through `customizeClone()`.
- `this` means "me", so `new GameBotCharacters(this)` reads as **"make a new bot that looks like me."**

---

## 7. Demo: `new` vs clone

```java
// The Expensive Way
for (int i = 0; i < numberOfBots; i++) {
    new GameBotCharacters("Bot" + i, 100, 0, new ArrayList<>(List.of("Rifle")));
}

// The Prototype Way
GameBotCharacters original = new GameBotCharacters("Bot1", 100, 0, new ArrayList<>(List.of("Rifle")));
for (int i = 1; i < numberOfBots; i++) {
    GameBotCharacters clone = original.customizeClone();
    clone.setName("Bot" + (i + 1));
    clone.setHealth(100 + i * 50);
    clone.setAttackPower(i * 10);
}
```

```
10008 ms to create 5 bots with new
2000 ms to create 5 bots with clone
```

- With `new`, you see the "Loading…" lines **5 times**. With cloning, **once**.
- The cost of cloning stays the same no matter how many bots you make. **Live tweak:** set `numberOfBots = 10` and re-run: about 20 s against still about 2 s.

---

## 8. Shallow copy vs deep copy (the most important slide)

A Java variable is a **label pointing to** an object, not the object itself.

- `name`, `health`, `attackPower` are simple values (`int`, and `String`, which can't be changed). Copying them is safe.
- `weapons` is a **List**, a separate object. Copying the *label* is not the same as copying the *list*.

| | Shallow copy | Deep copy |
|---|---|---|
| Code | `this.weapons = gbc.weapons;` | `this.weapons = new ArrayList<>(gbc.weapons);` |
| What you get | Two bots, **one shared** backpack | Two bots, **two separate** backpacks |
| Analogy | Two people sharing one Google Doc link | Each person downloads their own copy |

---

## 9. Demo: deep copy works

```java
GameBotCharacters deepClone = original.customizeClone();
original.getWeapons().add("Grenade");
```

```
Original:    [Rifle, Grenade]
Deep clone:  [Rifle]
```

The original picked up a grenade. The clone has its own backpack, so it's unaffected. ✅

---

## 10. Demo: the shallow copy bug

`shallowClone()` exists **only for this demo**. It shares the list on purpose:

```java
public GameBotCharacters shallowClone() {
    GameBotCharacters copy = new GameBotCharacters(this);
    copy.weapons = this.weapons; // both bots point at the SAME list
    return copy;
}
```

```
Original:      [Rifle, Grenade, Rocket Launcher]
Shallow clone: [Rifle, Grenade, Rocket Launcher]  <-- got the rocket launcher too!
Same list? true
```

- We gave the original a rocket launcher, and the clone got one too. ❌
- In a real game: every enemy suddenly has a rocket launcher because one of them picked it up.
- **Rule of thumb:** for every field that is a list, map, array or other object you can change, ask "should the clone get its own copy?" The answer is usually **yes**.

---

## 11. Prototype Registry: a shelf of templates

This is the CV / letter-template idea in code.

```java
BotRegistry registry = new BotRegistry();
registry.addTemplate("sniper", new GameBotCharacters("Sniper", 80, 90, ...));   // built once
registry.addTemplate("tank",   new GameBotCharacters("Tank", 500, 20, ...));    // built once

GameBotCharacters sniperA = registry.get("sniper");   // a clone
sniperA.setName("Sniper-Alpha");
GameBotCharacters sniperB = registry.get("sniper");   // another clone
sniperB.setName("Sniper-Bravo");
```

```java
public GameBotCharacters get(String key) {
    return templates.get(key).customizeClone();
}
```

- Each template is built **once** (the expensive part).
- `get()` **never** hands out the template itself, only a copy. So nobody can damage the template.
- Analogy: the **"Templates" gallery in Word / Google Docs**. You pick "Formal Letter", get your own copy, and the template stays clean for the next person.

---

## 12. Why not Java's built-in `clone()`?

Java has `Object.clone()` and the `java.lang.Cloneable` marker interface. Most experts (including *Effective Java* by Joshua Bloch) advise against them:

- `Cloneable` has **no methods**. It's just a flag that changes how `Object.clone()` behaves. Odd design.
- `clone()` throws a **checked** `CloneNotSupportedException` you must catch.
- It does a **shallow copy by default**, which is exactly the bug from slide 10.
- It creates the object **without calling any constructor**, which bypasses your setup and checks.

**Preferred instead:** a **copy constructor** or a **copy method**, which is exactly what we did.

---

## 13. Where you see it in real life

- **Document / design apps:** templates in Word, Google Docs, Canva, Figma ("Duplicate frame").
- **Games:** spawning enemies, bullets and trees from a loaded template (Unity's `Instantiate(prefab)` is the Prototype pattern).
- **Spring Framework:** `@Scope("prototype")` beans. Spring gives you a **new** instance every time instead of the shared singleton.
- **Java collections:** `new ArrayList<>(otherList)`, `new HashMap<>(otherMap)` are copy constructors.
- **Records & immutable objects:** "withers" such as `user.withEmail("new@mail.com")` return a modified copy.
- **Our Builder talk:** `toBuilder()` (copy an existing laptop, change a few things) is the same idea.

---

## 14. When to use it, and when not to

**Use Prototype when:**
- Creating an object is **expensive** (DB, network, file loading, heavy calculation).
- You need **many similar objects** that differ in only a few fields.
- You want to offer **ready-made templates** (registry).
- You want copies **without knowing the exact class** (the client only knows `Prototype<T>`).

**Skip it when:**
- Objects are cheap to create. `new` is simpler and clearer.
- The object has many nested, changeable parts. Deep copying everything correctly gets tricky.
- The object holds things that shouldn't be copied (open DB connections, file handles, threads).

---

## 15. Prototype vs the other creational patterns

| Pattern | The question it answers | Analogy |
|---|---|---|
| **Singleton** | "How do I make sure there's only **one**?" | The country's president |
| **Factory** | "**Which** kind of object should I create?" | A car dealership picks the model for your region |
| **Builder** | "**How** do I put a complex object together step by step?" | Ordering a custom burger |
| **Prototype** | "How do I make a new one **by copying** an existing one?" | Photocopying a letter template |

---

## 16. Likely questions (and short answers)

**Isn't cloning just calling `new` anyway?**
Yes, `customizeClone()` calls `new`, but on the **cheap copy constructor**, not the expensive one. The heavy work happens only once.

**What if the expensive part is shared data, like the animations?**
Then the clones can **share** it on purpose (a shallow copy for that field), since it's read-only. Deep-copy only what each clone will change.

**Why is the copy constructor `private`?**
So everyone outside goes through `customizeClone()`. That keeps one clear way of copying.

**Does the clone change if I change the original later?**
Not with a deep copy. With a shallow copy, any shared lists/objects will change. That's the bug from slide 10.

**Prototype vs Singleton?**
Opposites. Singleton: "always give me **the same** one." Prototype: "always give me **a new copy**."

**Is it thread-safe?**
Copying is safe as long as nobody is changing the prototype at the same time. Treat registry templates as **read-only** and you're fine.

---

## 17. Things to tweak live

| Tweak | Where | What the audience sees |
|---|---|---|
| `numberOfBots = 10` | `Main.prototypeDemo()` | `new` time grows, clone time stays ~2 s |
| `LOADING_TIME_MS = 500` | `GameBotCharacters` | Faster demo if you're short on time |
| Change `new ArrayList<>(gbc.weapons)` to `gbc.weapons` | Copy constructor | Slide 9's deep copy demo breaks: the clone gets the grenade too |
| Add a `"medic"` template | `prototypeDemo()` section 5 | The registry handles new templates without any other changes |
| Add a new field (e.g. `int speed`) | `GameBotCharacters` | Point out: **forget it in the copy constructor and clones silently lose it** |

---

## 18. Summary

- **Problem:** some objects are expensive to create, and we need many similar ones.
- **Idea:** create one **prototype** the hard way, then **clone and tweak**.
- **How:** a `Prototype<T>` interface + a private copy constructor.
- **Watch out:** shallow vs deep copy. Give each clone its own copy of anything it can change.
- **Bonus:** a **registry** of templates (CVs, letters, bot types) that hands out copies, never the original.
