package com.example.BuilderPattern.fluent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class Laptop {
    private final String brand;
    private final String cpu;
    private final int ramGb;
    private final int storageGb;
    private final boolean dedicatedGpu;
    private final String os;
    private final List<String> accessories;

    // Private: the only way to get a Laptop is through the Builder
    private Laptop(Builder builder) {
        this.brand = builder.brand;
        this.cpu = builder.cpu;
        this.ramGb = builder.ramGb;
        this.storageGb = builder.storageGb;
        this.dedicatedGpu = builder.dedicatedGpu;
        this.os = builder.os;
        this.accessories = List.copyOf(builder.accessories);
    }

    // Required fields go here; everything else is optional
    public static Builder builder(String brand, String cpu) {
        return new Builder(brand, cpu);
    }

    // Copy this laptop into a new builder so we can make a variant of it
    public Builder toBuilder() {
        Builder b = new Builder(brand, cpu);
        b.ramGb = ramGb;
        b.storageGb = storageGb;
        b.dedicatedGpu = dedicatedGpu;
        b.os = os;
        b.accessories.addAll(accessories);
        return b;
    }

    @Override
    public String toString() {
        return brand + " | " + cpu + " | " + ramGb + "GB RAM | " + storageGb + "GB | GPU: "
                + (dedicatedGpu ? "dedicated" : "integrated") + " | " + os + " | " + accessories;
    }

    public static final class Builder {
        private final String brand;
        private final String cpu;
        private int ramGb = 8;
        private int storageGb = 256;
        private boolean dedicatedGpu = false;
        private String os = "Linux";
        private final List<String> accessories = new ArrayList<>();

        private Builder(String brand, String cpu) {
            this.brand = Objects.requireNonNull(brand, "brand is required");
            this.cpu = Objects.requireNonNull(cpu, "cpu is required");
        }

        public Builder ram(int gb) {
            this.ramGb = gb;
            return this;
        }

        public Builder storage(int gb) {
            this.storageGb = gb;
            return this;
        }

        public Builder withDedicatedGpu() {
            this.dedicatedGpu = true;
            return this;
        }

        public Builder os(String os) {
            this.os = os;
            return this;
        }

        public Builder addAccessory(String accessory) {
            this.accessories.add(accessory);
            return this;
        }

        // Validate everything in one place before handing out the object
        public Laptop build() {
            if (ramGb < 4) {
                throw new IllegalStateException("RAM must be at least 4GB");
            }
            if (dedicatedGpu && ramGb < 16) {
                throw new IllegalStateException("A dedicated GPU needs at least 16GB RAM");
            }
            return new Laptop(this);
        }
    }
}
