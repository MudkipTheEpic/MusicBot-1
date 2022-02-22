package com.jagrosh.jmusicbot;

import com.jagrosh.jmusicbot.audio.AudioHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MediaControl extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        Message message = event.getMessage();
        if (!message.isWebhookMessage()) {
            return;
        }
        AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
        AudioPlayer player = handler.getPlayer();

        if (message.getContentDisplay().equals("play")) {
            player.setPaused(false);
        } else if (message.getContentDisplay().equals("pause")) {
            player.setPaused(true);
        } else if (message.getContentDisplay().equals("switchplaypause")) {
            player.setPaused(!player.isPaused());
        } else if (message.getContentDisplay().equals("skip")) {
            player.stopTrack();
        }
    }
}
