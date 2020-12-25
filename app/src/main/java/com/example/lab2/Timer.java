package com.example.lab2;

public class Timer {
    private final long id;
    private final String title;
    private final int ready, work, relax, cycles, sets, relax_sets, color;

    Timer(long id, String title, int ready, int work, int relax, int cycles, int sets,
          int relax_sets, int color) {
        this.id = id;
        this.title = title;
        this.ready = ready;
        this.work = work;
        this.relax = relax;
        this.cycles = cycles;
        this.sets = sets;
        this.relax_sets = relax_sets;
        this.color = color;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getReady() {
        return ready;
    }

    public int getWork() {
        return work;
    }

    public int getRelax() {
        return relax;
    }

    public int getCycles() {
        return cycles;
    }

    public int getSets() {
        return sets;
    }

    public int getRelax_sets() {
        return relax_sets;
    }

    public int getTimerColor() {
        return color;
    }

}
