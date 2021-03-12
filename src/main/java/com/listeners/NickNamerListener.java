package com.listeners;

import org.bukkit.event.EventHandler;
import org.inventivetalent.nicknamer.api.event.disguise.NickDisguiseEvent;
import org.inventivetalent.nicknamer.api.event.disguise.SkinDisguiseEvent;

public class NickNamerListener {

    @EventHandler
    public void on(NickDisguiseEvent event) {
        //Todo - to poeple of the same faction, show the normal skin
        //todo - to people of the oppising faction, show the other skin

        System.out.println("Nick disguise event called");
        event.setNick("Poopoopoo");
    }


    @EventHandler
    public void on(SkinDisguiseEvent event) {
        System.out.println("Skin disguise event called");
    }
}
