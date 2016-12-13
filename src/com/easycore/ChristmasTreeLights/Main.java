package com.easycore.ChristmasTreeLights;

import com.easycore.ChristmasTreeLights.firebase.ChangeWatcher;
import com.easycore.ChristmasTreeLights.helper.Utils;

public class Main {

    public static void main(String[] args) {
        App app = new App();
        ChangeWatcher watcher;
        try {
            app.parseArguments(args);
            watcher = app.prepareWatcher();
        } catch (App.HelpException e) {
            e.exit();
            return;
        } catch (Exception e) {
            System.err.printf("Launch error: \"%s.\"\n", e.getMessage());
            System.exit(1);
            return;
        }

        System.out.println("Application prepare successful. ");
        watcher.watchRequests();

        while (true) {
            Utils.fatalSleep(Long.MAX_VALUE);
        }
    }

}
