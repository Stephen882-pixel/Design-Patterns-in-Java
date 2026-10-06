package com.example.BuilderPattern.classic;

public class ComputerDirector {
    // Recipe 1: a complete computer
    public void construct(Builder builder){
        builder.buildCPU();
        builder.buildRAM();
        builder.buildStorage();
    }

    // Recipe 2: a basic computer (no storage)
    public void constructBasic(Builder builder){
        builder.buildCPU();
        builder.buildRAM();
    }
}
