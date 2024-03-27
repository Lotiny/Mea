package me.lotiny.mea.utils;

import lombok.experimental.UtilityClass;
import me.lotiny.mea.Mea;

@UtilityClass
public class Tasks {

    private final Mea plugin = Mea.getInstance();

    /**
     * Run a task synchronously.
     *
     * @param callable The code to execute as a task.
     */
    public void run(Callable callable) {
        plugin.getServer().getScheduler().runTask(plugin, callable::call);
    }

    /**
     * Run a task asynchronously.
     *
     * @param callable The code to execute as a task.
     */
    public void runAsync(Callable callable) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, callable::call);
    }

    /**
     * Run a task later with a specified delay.
     *
     * @param callable The code to execute as a task.
     * @param delay    The delay (in ticks) before the task is executed.
     */
    public void runLater(Callable callable, long delay) {
        plugin.getServer().getScheduler().runTaskLater(plugin, callable::call, delay);
    }

    /**
     * Run a task asynchronously later with a specified delay.
     *
     * @param callable The code to execute as a task.
     * @param delay    The delay (in ticks) before the task is executed.
     */
    public void runAsyncLater(Callable callable, long delay) {
        plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, callable::call, delay);
    }

    /**
     * The `Callable` functional interface defines a single method for executing a task.
     */
    public interface Callable {
        void call();
    }
}
