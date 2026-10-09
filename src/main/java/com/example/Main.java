package com.example;


import com.example.AbstractFactory.AfricaCarFactory;
import com.example.AbstractFactory.Car;
import com.example.AbstractFactory.CarFactory;
import com.example.AbstractFactory.CarSpecification;
import com.example.AbstractFactory.EuropeCarFactory;
import com.example.AbstractFactory.NorthAmericaCarFactory;
import com.example.BuilderPattern.classic.BudgetComputerBuilder;
import com.example.BuilderPattern.classic.Builder;
import com.example.BuilderPattern.classic.Computer;
import com.example.BuilderPattern.classic.ComputerDirector;
import com.example.BuilderPattern.classic.GamingComputerBuilder;
import com.example.BuilderPattern.classic.OfficeComputerBuilder;
import com.example.BuilderPattern.fluent.Laptop;
import com.example.FactoryMethod.EmailService;
import com.example.FactoryMethod.NotificationService;
import com.example.FactoryMethod.PushService;
import com.example.FactoryMethod.SmsService;
import com.example.PrototypeDesignPattern.BotRegistry;
import com.example.PrototypeDesignPattern.GameBotCharacters;
import com.example.Singleton.BillPughSingleton;
import com.example.Singleton.Calculator;
import com.example.Singleton.DoubleCheckingSingleton;
import com.example.Singleton.EagerSingleton;
import com.example.Singleton.EnumSingleton;
import com.example.Singleton.LazySingleton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.function.Supplier;

public class Main {
    public static void main(String[] args) {
        // Uncomment the pattern you want to demo (in talk order)
        singletonDemo();
//        factoryMethodDemo();
//        abstractFactoryDemo();
//        classicBuilderDemo();
//        fluentBuilderDemo();
//        prototypeDemo();
    }

    // ======================================================================
    // SINGLETON
    // ======================================================================
    private static void singletonDemo() {
        printTitle("SINGLETON DESIGN PATTERN");

        printSection("1. Eager Singleton");
        System.out.println("Same object? " + (EagerSingleton.getInstance() == EagerSingleton.getInstance()));

        printSection("2. Lazy Singleton (synchronized)");
        System.out.println("Same object? " + (LazySingleton.getInstance() == LazySingleton.getInstance()));

        printSection("3. Double-Checked Locking");
        System.out.println("Same object? " + (DoubleCheckingSingleton.getInstance() == DoubleCheckingSingleton.getInstance()));

        printSection("4. Bill Pugh (Holder class)");
        System.out.println("Same object? " + (BillPughSingleton.getInstance() == BillPughSingleton.getInstance()));

        printSection("5. Enum Singleton: shared state");
        System.out.println("Customer A gets ticket #" + EnumSingleton.INSTANCE.nextTicket());
        System.out.println("Customer B gets ticket #" + EnumSingleton.INSTANCE.nextTicket());
        System.out.println("Customer C gets ticket #" + EnumSingleton.INSTANCE.nextTicket());

        printSection("6. The Race: 10 threads, naive lazy vs thread-safe");
        // Tweak this and re-run
        int threads = 10;
        System.out.println("Naive (Calculator):     " + countInstances(threads, Calculator::getInstance) + " different object(s)");
        System.out.println("Thread-safe (BillPugh): " + countInstances(threads, BillPughSingleton::getInstance) + " different object(s)");
    }

    // Calls getInstance() from many threads at the same moment and counts the distinct objects returned
    private static int countInstances(int threads, Supplier<Object> getInstance) {
        Set<Object> instances = ConcurrentHashMap.newKeySet();
        CountDownLatch startGun = new CountDownLatch(1);
        List<Thread> workers = new ArrayList<>();
        for (int i = 0; i < threads; i++) {
            Thread t = new Thread(() -> {
                try {
                    startGun.await();
                } catch (InterruptedException e) {
                    return;
                }
                instances.add(getInstance.get());
            });
            t.start();
            workers.add(t);
        }
        startGun.countDown();
        for (Thread t : workers) {
            try {
                t.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        return instances.size();
    }

    // ======================================================================
    // FACTORY METHOD
    // ======================================================================
    private static void factoryMethodDemo() {
        printTitle("FACTORY METHOD DESIGN PATTERN");

        printSection("1. Each service creates its own kind of notification");
        new EmailService().notifyUser("Your invoice is ready");
        new SmsService().notifyUser("Your invoice is ready");
        new PushService().notifyUser("Your invoice is ready");

        printSection("2. Picking the service at runtime (e.g. from user settings)");
        // Tweak these and re-run
        List<Map.Entry<String, String>> preferredChannel = List.of(
                Map.entry("Alice", "email"),
                Map.entry("Brian", "sms"),
                Map.entry("Wanjiku", "push")
        );
        for (Map.Entry<String, String> user : preferredChannel) {
            NotificationService service = switch (user.getValue()) {
                case "email" -> new EmailService();
                case "sms" -> new SmsService();
                case "push" -> new PushService();
                default -> throw new IllegalArgumentException("Unknown channel " + user.getValue());
            };
            service.notifyUser("Hi " + user.getKey() + ", your payment was received");
        }
    }

    // ======================================================================
    // ABSTRACT FACTORY
    // ======================================================================
    private static void abstractFactoryDemo() {
        printTitle("ABSTRACT FACTORY DESIGN PATTERN");

        printSection("1. North America Factory");
        deliverCar(new NorthAmericaCarFactory());

        printSection("2. Europe Factory");
        deliverCar(new EuropeCarFactory());

        printSection("3. Africa Factory (added later, client code unchanged)");
        deliverCar(new AfricaCarFactory());

        printSection("4. Picking the factory from configuration");
        // Tweak this and re-run: "north-america", "europe" or "africa"
        String region = "africa";
        CarFactory factory = switch (region) {
            case "north-america" -> new NorthAmericaCarFactory();
            case "europe" -> new EuropeCarFactory();
            case "africa" -> new AfricaCarFactory();
            default -> throw new IllegalArgumentException("Unknown region " + region);
        };
        deliverCar(factory);
    }

    // The client: only knows the interfaces, never Sedan, Hatchback or Pickup
    private static void deliverCar(CarFactory factory) {
        Car car = factory.createCar();
        CarSpecification spec = factory.createCarSpecification();
        car.assemble();
        spec.display();
    }

    // ======================================================================
    // BUILDER - CLASSIC (Director + Builder interface)
    // ======================================================================
    private static void classicBuilderDemo() {
        printTitle("BUILDER DESIGN PATTERN - CLASSIC");
        ComputerDirector director = new ComputerDirector();

        printSection("1. Gaming Computer");
        GamingComputerBuilder gamingBuilder = new GamingComputerBuilder();
        director.construct(gamingBuilder);
        Computer gamingComputer = gamingBuilder.getResult();
        gamingComputer.displayInfo();

        printSection("2. Office Computer");
        OfficeComputerBuilder officeBuilder = new OfficeComputerBuilder();
        director.construct(officeBuilder);
        Computer officeComputer = officeBuilder.getResult();
        officeComputer.displayInfo();

        printSection("3. Same Director, Many Builders (loop)");
        List<Builder> builders = List.of(
                new GamingComputerBuilder(),
                new OfficeComputerBuilder(),
                new BudgetComputerBuilder()
        );
        for (Builder builder : builders) {
            director.construct(builder);
            builder.getResult().displayInfo();
        }

        printSection("4. Same Builder, Different Recipe (constructBasic)");
        Builder basicBuilder = new OfficeComputerBuilder();
        director.constructBasic(basicBuilder);
        basicBuilder.getResult().displayInfo();

        printSection("5. Reusing a Builder (getResult resets)");
        GamingComputerBuilder reusedBuilder = new GamingComputerBuilder();
        director.construct(reusedBuilder);
        Computer pc1 = reusedBuilder.getResult();
        director.construct(reusedBuilder);
        Computer pc2 = reusedBuilder.getResult();
        System.out.println("Same object? " + (pc1 == pc2));
    }

    // ======================================================================
    // BUILDER - FLUENT (static nested Builder, immutable object)
    // ======================================================================
    private static void fluentBuilderDemo() {
        printTitle("BUILDER DESIGN PATTERN - FLUENT");

        printSection("1. Different Laptops From The Same Builder");
        Laptop student = Laptop.builder("Lenovo", "Ryzen 5").build();

        Laptop developer = Laptop.builder("Dell", "Intel i7")
                .ram(32)
                .storage(1024)
                .addAccessory("External monitor")
                .addAccessory("Mechanical keyboard")
                .build();

        Laptop gamer = Laptop.builder("ASUS", "Intel i9")
                .ram(32)
                .withDedicatedGpu()
                .os("Windows 11")
                .build();

        System.out.println("Student:   " + student);
        System.out.println("Developer: " + developer);
        System.out.println("Gamer:     " + gamer);

        printSection("2. Validation In build()");
        try {
            Laptop.builder("HP", "Intel i3").withDedicatedGpu().build();
        } catch (IllegalStateException e) {
            System.out.println("Rejected: " + e.getMessage());
        }

        printSection("3. Building Dynamically From Data");
        List<Map<String, String>> orders = List.of(
                Map.of("brand", "Dell", "cpu", "Intel i5", "ram", "16"),
                Map.of("brand", "Apple", "cpu", "M3", "ram", "24", "os", "macOS"),
                Map.of("brand", "MSI", "cpu", "Ryzen 9", "ram", "32", "gpu", "true"),
                Map.of("brand", "Lenovo", "cpu", "Ryzen 5")
        );
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

        printSection("4. Variants With toBuilder()");
        Laptop base = Laptop.builder("Dell", "Intel i7").ram(16).storage(512).build();
        Laptop devVariant = base.toBuilder().ram(32).addAccessory("Docking station").build();
        Laptop gamingVariant = base.toBuilder().withDedicatedGpu().os("Windows 11").build();
        System.out.println("Base:    " + base);
        System.out.println("Dev:     " + devVariant);
        System.out.println("Gaming:  " + gamingVariant);
    }

    // ======================================================================
    // PROTOTYPE
    // ======================================================================
    private static void prototypeDemo() {
        printTitle("PROTOTYPE DESIGN PATTERN");
        // Tweak these and re-run
        int numberOfBots = 5;

        printSection("1. The Expensive Way: new for every bot");
        long start = System.currentTimeMillis();
        for (int i = 0; i < numberOfBots; i++) {
            new GameBotCharacters("Bot" + i, 100, 0, new ArrayList<>(List.of("Rifle")));
        }
        System.out.println((System.currentTimeMillis() - start) + " ms to create " + numberOfBots + " bots with new");

        printSection("2. The Prototype Way: create once, clone the rest");
        start = System.currentTimeMillis();
        GameBotCharacters original = new GameBotCharacters("Bot1", 100, 0, new ArrayList<>(List.of("Rifle")));
        List<GameBotCharacters> bots = new ArrayList<>();
        bots.add(original);
        for (int i = 1; i < numberOfBots; i++) {
            GameBotCharacters clone = original.customizeClone();
            clone.setName("Bot" + (i + 1));
            clone.setHealth(100 + i * 50);
            clone.setAttackPower(i * 10);
            bots.add(clone);
        }
        System.out.println((System.currentTimeMillis() - start) + " ms to create " + numberOfBots + " bots with clone");
        bots.forEach(System.out::println);

        printSection("3. Deep Copy: changing the original doesn't touch the clones");
        GameBotCharacters deepClone = original.customizeClone();
        original.getWeapons().add("Grenade");
        System.out.println("Original:    " + original.getWeapons());
        System.out.println("Deep clone:  " + deepClone.getWeapons());

        printSection("4. Shallow Copy Bug: the list is shared");
        GameBotCharacters shallowClone = original.shallowClone();
        original.getWeapons().add("Rocket Launcher");
        System.out.println("Original:      " + original.getWeapons());
        System.out.println("Shallow clone: " + shallowClone.getWeapons() + "  <-- got the rocket launcher too!");
        System.out.println("Same list? " + (original.getWeapons() == shallowClone.getWeapons()));

        printSection("5. Prototype Registry: a shelf of ready-made templates");
        BotRegistry registry = new BotRegistry();
        registry.addTemplate("sniper", new GameBotCharacters("Sniper", 80, 90, new ArrayList<>(List.of("Sniper Rifle"))));
        registry.addTemplate("tank", new GameBotCharacters("Tank", 500, 20, new ArrayList<>(List.of("Shotgun", "Shield"))));

        GameBotCharacters sniperA = registry.get("sniper");
        sniperA.setName("Sniper-Alpha");
        GameBotCharacters sniperB = registry.get("sniper");
        sniperB.setName("Sniper-Bravo");
        GameBotCharacters tank = registry.get("tank");
        tank.setName("Tank-Charlie");
        System.out.println(sniperA);
        System.out.println(sniperB);
        System.out.println(tank);
    }

    // ======================================================================
    // HELPERS
    // ======================================================================
    private static void printTitle(String title) {
        System.out.println();
        System.out.println("================ " + title + " ================");
    }

    private static void printSection(String section) {
        System.out.println();
        System.out.println("------- " + section + " -------");
    }
}
