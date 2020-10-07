package com.jagrosh.jmusicbot.commands.dj;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.JMusicBot;
import com.jagrosh.jmusicbot.audio.AudioHandler;
import com.jagrosh.jmusicbot.commands.DJCommand;
import com.jagrosh.jmusicbot.settings.Settings;
import com.jagrosh.jmusicbot.utils.FormatUtil;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SeekCmd extends DJCommand
{

    private final Pattern timestampPattern = Pattern.compile("^(\\d+):([0-5]\\d)$");
    private final Pattern simplePattern = Pattern.compile("^(\\d+)([smh]?)$");
    public SeekCmd(Bot bot)
    {
        super(bot);
        this.name = "seek";
        this.aliases = new String[]{};
        this.help = "Seeks to a specified point, or forwards/backwards through a song";
        this.arguments = "<mm:ss> or <##s/m/h>";
    }

    @Override
    public void doCommand(CommandEvent event)
    {
        AudioHandler handler = (AudioHandler)event.getGuild().getAudioManager().getSendingHandler();
        AudioTrack track = handler.getPlayer().getPlayingTrack();
        boolean relative = false;

        long seekPosition;
        String input = event.getArgs().trim();

        if (input.startsWith("+")) {
            relative = true;
            seekPosition = parseTime(input.substring(1));
        } else if (input.startsWith("-")) {
            relative = true;
            seekPosition = parseTime(input.substring(1)) * -1;
        } else {
            seekPosition = parseTime(input);
        }

        if(seekPosition == -1) {
            event.replyError("Please include an absolute or relative time to seek to!");
            return;
        } else if (track == null) {
            event.replyError("Cannot seek: nothing playing.");
            return;
        } else if (!track.isSeekable()) {
            event.replyError("Streams are not seekable.");
            return;
        }

        long boundedSeekPosition;

        if (relative) {
            boundedSeekPosition = Math.max(0, Math.min(track.getDuration(), track.getPosition()+seekPosition));
        } else {
            boundedSeekPosition = Math.max(0, Math.min(track.getDuration(), seekPosition));
        }

        boolean isForward = boundedSeekPosition >= track.getPosition();
        String labelEmoji;

        if (boundedSeekPosition > track.getPosition()) {
            labelEmoji = JMusicBot.FORWARD_EMOJI;
        } else if (boundedSeekPosition < track.getPosition()) {
            labelEmoji = JMusicBot.BACKWARDS_EMOJI;
        } else {
            labelEmoji = "";
        }

        track.setPosition(boundedSeekPosition);

        int newMinutes = ((int) boundedSeekPosition / 1000) / 60;
        int newSeconds = ((int) boundedSeekPosition / 1000) % 60;

        String formattedTime = String.format("%d:%02d", newMinutes, newSeconds);

        event.reply(labelEmoji + " Seeked " + (isForward ? "forwards" : "backwards") + " to " + formattedTime + "...");

        /*if(nvolume<0 || nvolume>150)
            event.reply(event.getClient().getError()+" Volume must be a valid integer between 0 and 150!");
        else
        {
            handler.getPlayer().setVolume(nvolume);
            settings.setVolume(nvolume);
            event.reply(FormatUtil.volumeIcon(nvolume)+" Volume changed from `"+volume+"` to `"+nvolume+"`");
        }*/
    }

    private long parseTime(String timeString) {
        Matcher timeStampMatcher = timestampPattern.matcher(timeString);
        Matcher simpleMatcher = simplePattern.matcher(timeString);
        int minutes, seconds;
        if (timeStampMatcher.matches()) {
            try {
                minutes = Integer.parseInt(timeStampMatcher.group(1));
                seconds = Integer.parseInt(timeStampMatcher.group(2));
            } catch (NumberFormatException e) {
                return -1;
            }
        } else if (simpleMatcher.matches()) {
            try {
                minutes = 0;
                seconds = Integer.parseInt(simpleMatcher.group(1));
                if (simpleMatcher.group(2) != null) {
                    switch (simpleMatcher.group(2)) {
                        case "m":
                            seconds *= 60;
                            break;
                        case "h":
                            seconds *= 3600;
                            break;
                    }
                }
            } catch (NumberFormatException e) {
                return -1;
            }
        } else {
            return -1;
        }
        return (long) (minutes * 60 + seconds) * 1000;
    }

}
