package com.example;


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
import com.example.Singleton.DoubleCheckingSingleton;
import com.example.Singleton.EagerSingleton;
import com.example.Singleton.LazySingleton;

import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        // Uncomment the pattern you want to demo
   //     singletonDemo();
 //       abstractFactoryDemo();
     //   classicBuilderDemo();
        fluentBuilderDemo();
    }

    // ======================================================================
    // SINGLETON
    // ======================================================================
    private static void singletonDemo() {
        printTitle("SINGLETON DESIGN PATTERN");

        printSection("Eager Singleton");
        EagerSingleton obj1 = EagerSingleton.getInstance();
        EagerSingleton obj2 = EagerSingleton.getInstance();
        System.out.println(obj1.hashCode());
        System.out.println(obj2.hashCode());

        printSection("Lazy Singleton");
        LazySingleton obj3 = LazySingleton.getInstance();
        LazySingleton obj4 = LazySingleton.getInstance();
        System.out.println(obj3.hashCode());
        System.out.println(obj4.hashCode());

        printSection("Double Checking Singleton");
        DoubleCheckingSingleton obj5 = DoubleCheckingSingleton.getInstance();
        DoubleCheckingSingleton obj6 = DoubleCheckingSingleton.getInstance();
        System.out.println(obj5.hashCode());
        System.out.println(obj6.hashCode());
    }

    // ======================================================================
    // ABSTRACT FACTORY
    // ======================================================================
    private static void abstractFactoryDemo() {
        printTitle("ABSTRACT FACTORY DESIGN PATTERN");

        printSection("North America Factory");
        CarFactory northAmericaFactory = new NorthAmericaCarFactory();
        Car northAmericaCar = northAmericaFactory.createCar();
        CarSpecification northAmericaSpec = northAmericaFactory.createCarSpecification();
        northAmericaCar.assemble();
        northAmericaSpec.display();

        printSection("Europe Factory");
        CarFactory europeFactory = new EuropeCarFactory();
        Car europeCar = europeFactory.createCar();
        CarSpecification europeSpec = europeFactory.createCarSpecification();
        europeCar.assemble();
        europeSpec.display();
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
