# The Builder Design Pattern in Java

Notes for the "Design Patterns in Java" session (Saturday, 10 October 2026).
Each `##` heading below is meant to become one slide.

Code lives in:
- `src/main/java/com/example/BuilderPattern/classic/` – the classic (Gang of Four) version
- `src/main/java/com/example/BuilderPattern/fluent/` – the modern fluent version
- `src/main/java/com/example/Main.java` – the client (`classicBuilderDemo()` and `fluentBuilderDemo()`)

---

## 1. What problem are we solving?

Some objects have **many parts**, and most of those parts are **optional**.

Imagine creating a laptop with a constructor:

```java
new Laptop("Dell", "Intel i7", 32, 1024, false, "Linux", accessories);
```

- What does `32` mean? What does `false` mean? You can't tell without opening the class.
- Swap `32` and `1024` by mistake and it still compiles. You just get a wrong laptop.
- Want a laptop without accessories? You need yet another constructor.

This is called the **telescoping constructor problem**: more and more constructors, each with more parameters.

---

## 2. The idea in one sentence

> **Separate *how* an object is put together from the object itself, so you can build it step by step.**

Real-life analogy: **ordering a burger.**
You don't shout every ingredient at once in a fixed order. You say: "bun… beef patty… cheese… no onions… extra sauce… done!"
The kitchen (the *builder*) collects your choices and only hands you the burger when you say "done" (`build()`).

---

## 3. Two flavours of Builder

| | Classic (Gang of Four) | Fluent (modern Java) |
|---|---|---|
| Our example | `Computer` | `Laptop` |
| Who decides the steps? | A **Director** | The **client** (you), by chaining calls |
| Typical use | Reusable "recipes" (gaming PC, office PC) | Objects with many optional fields |
| Seen in real code | Less common | Very common (`StringBuilder`, `HttpRequest`, Lombok) |

---

## 4. Classic Builder – the four players

| Role | Our class | Layman's job |
|---|---|---|
| **Product** | `Computer` | The thing being built |
| **Builder** (interface) | `Builder` | The "job description": every builder must know how to add a CPU, RAM and storage |
| **Concrete Builder** | `GamingComputerBuilder`, `OfficeComputerBuilder`, `BudgetComputerBuilder` | The workers. Each one picks *which* parts to use |
| **Director** | `ComputerDirector` | The foreman. Knows the *order of steps*, but not the actual parts |

---

## 5. Classic Builder – the code

```java
public interface Builder {
    void buildCPU();
    void buildRAM();
    void buildStorage();
    Computer getResult();
}
```

```java
public class ComputerDirector {
    public void construct(Builder builder){
        builder.buildCPU();
        builder.buildRAM();
        builder.buildStorage();
    }
}
```

Client:

```java
GamingComputerBuilder gamingBuilder = new GamingComputerBuilder();
director.construct(gamingBuilder);
Computer gamingComputer = gamingBuilder.getResult();
```

---

## 6. Same steps, different results

```java
List<Builder> builders = List.of(
        new GamingComputerBuilder(),
        new OfficeComputerBuilder(),
        new BudgetComputerBuilder()
);
for (Builder builder : builders) {
    director.construct(builder);
    builder.getResult().displayInfo();
}
```

- The director runs the **same three steps** every time.
- Each builder puts **different parts** in, so we get three different computers.
- New type of computer? **Add a new builder.** The director doesn't change.
  (This is the *Open/Closed Principle*: open for extension, closed for modification.)

---

## 7. Same builder, different recipe

The director can have more than one recipe:

```java
public void constructBasic(Builder builder){
    builder.buildCPU();
    builder.buildRAM();
}
```

Result: `Storage: null`.

- The director controls **which steps run, and in what order**.
- The `null` also shows a **weakness** of the classic style: nothing stops you from building an incomplete computer. The fluent builder fixes this (slide 12).

---

## 8. The `getResult()` trap

```java
public Computer getResult(){
    Computer result = computer;   // 1. pick up the finished computer
    computer = new Computer();    // 2. put a fresh empty one on the workbench
    return result;                // 3. hand over the finished one
}
```

**Workbench analogy:** the builder is a carpenter, and `computer` is whatever is on their workbench.

- `private Computer computer = new Computer();` puts the **first** empty case on the bench.
- `getResult()` hands over the finished one and puts a **new** empty case on the bench for the next order.

**Without step 2**, the carpenter keeps working on the computer they already gave away:

```java
Computer pc1 = builder.getResult();
director.construct(builder);          // modifies pc1 again!
Computer pc2 = builder.getResult();
pc1 == pc2   // true – you have ONE computer with two labels
```

**Key idea:** a Java variable is a **label pointing to** an object, not the object itself.
`Computer result = computer;` adds a second label to the same object. It doesn't make a copy.

---

## 9. Fluent Builder – what it looks like

```java
Laptop developer = Laptop.builder("Dell", "Intel i7")
        .ram(32)
        .storage(1024)
        .addAccessory("External monitor")
        .addAccessory("Mechanical keyboard")
        .build();
```

It reads almost like English. Every value has a **name** next to it.

---

## 10. Fluent Builder – how the chaining works

```java
public Builder ram(int gb) {
    this.ramGb = gb;
    return this;      // ← hand the same builder back
}
```

Each method **returns the builder itself**, so you can call the next method on it immediately:
`builder.ram(32)` gives back `builder`, then `.storage(1024)` gives back `builder`, and so on.

Then `build()` takes everything collected and creates the `Laptop`.

---

## 11. Fluent Builder – the anatomy of `Laptop`

| Piece | Why it's there |
|---|---|
| `private Laptop(Builder builder)` | Private constructor, so **the only way** to get a Laptop is through the builder |
| `private final` fields, no setters | **Immutable**: once built, a laptop can't be changed by accident |
| `public static final class Builder` | The builder lives inside the class it builds (a *static nested class*) |
| `Laptop.builder(brand, cpu)` | **Required** fields go here. You can't forget them |
| `ramGb = 8`, `os = "Linux"`, … | **Optional** fields have sensible defaults |
| `List.copyOf(...)` | The laptop gets its own copy of the list, so later changes to the builder don't leak in |

---

## 12. Validation in `build()`

```java
public Laptop build() {
    if (ramGb < 4) {
        throw new IllegalStateException("RAM must be at least 4GB");
    }
    if (dedicatedGpu && ramGb < 16) {
        throw new IllegalStateException("A dedicated GPU needs at least 16GB RAM");
    }
    return new Laptop(this);
}
```

```
Rejected: A dedicated GPU needs at least 16GB RAM
```

- All the rules are checked **in one place, before** the object exists.
- So **an invalid Laptop can never exist.** Compare that with `Storage: null` from slide 7.

---

## 13. Building objects dynamically from data

```java
for (Map<String, String> order : orders) {
    Laptop.Builder builder = Laptop.builder(order.get("brand"), order.get("cpu"));
    if (order.containsKey("ram")) {
        builder.ram(Integer.parseInt(order.get("ram")));
    }
    if (order.containsKey("os")) {
        builder.os(order.get("os"));
    }
    if ("true".equals(order.get("gpu"))) {
        builder.withDedicatedGpu();
    }
    System.out.println(builder.build());
}
```

- The orders could come from a web form, a JSON file or a database.
- A builder lets you **add options one at a time, only if they're present**.
- A constructor can't do that, because it needs every value in one single call.

---

## 14. Making variants with `toBuilder()`

```java
Laptop base          = Laptop.builder("Dell", "Intel i7").ram(16).storage(512).build();
Laptop devVariant    = base.toBuilder().ram(32).addAccessory("Docking station").build();
Laptop gamingVariant = base.toBuilder().withDedicatedGpu().os("Windows 11").build();
```

- `toBuilder()` copies an existing laptop's settings into a new builder.
- Change only what you need, then `build()` a **new** laptop.
- `base` is never touched. This is how you "modify" something that can't be changed: **copy it with differences.**

---

## 15. You already use builders

```java
String s = new StringBuilder().append("Hello").append(", ").append("World").toString();

HttpRequest req = HttpRequest.newBuilder()
        .uri(URI.create("https://example.com"))
        .header("Accept", "application/json")
        .GET()
        .build();

Stream<String> stream = Stream.<String>builder().add("a").add("b").build();
```

Also: **Lombok `@Builder`** generates the whole fluent builder from one annotation
(`@Builder(toBuilder = true)` gives you `toBuilder()` too).

---

## 16. When to use it, and when not to

**Use a Builder when:**
- The object has **many fields** (roughly 4+), especially optional ones
- You want the object to be **immutable**
- You need to **validate** combinations of fields
- You build objects **step by step** or **from data**

**Skip it when:**
- The class has only 2–3 fields. A constructor (or a Java `record`) is simpler.
- Adding a builder would only add boilerplate.

---

## 17. Likely questions (and short answers)

**Why not just use a constructor?**
With many parameters it's hard to read and easy to mix up. The builder names every value, lets you skip optional ones and validates at the end.

**Why not just use setters?**
With setters the object exists **half-built**, and anyone can change it later. A builder hands you a **complete, unchangeable** object.

**Builder vs Factory?**
A **Factory** decides *which* object to create, in one call ("give me a car for Europe").
A **Builder** controls *how* one complex object is put together, step by step ("this CPU, this much RAM, then build").

**Is the Director required?**
No. It's useful for reusable recipes. Most modern code skips it and chains calls on a fluent builder directly.

**Is a builder thread-safe?**
The **builder** isn't, so don't share one between threads. The **object it builds** is immutable, so that object is safe to share.

**Why is `Builder` a `static` nested class?**
So you can create it **without** having a `Laptop` first (`Laptop.builder(...)`). A non-static inner class would need an existing `Laptop` object.

---

## 18. Summary

- **Problem:** objects with many (optional) parts make messy constructors.
- **Idea:** build the object **step by step**, then call `build()`.
- **Classic version:** Director (the steps) + Builders (the parts) → same process, different products.
- **Fluent version:** chained methods + `build()` → readable, validated, immutable objects.
- **Bonus:** build objects dynamically from data, and make variants with `toBuilder()`.
