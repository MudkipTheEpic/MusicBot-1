/*
 * Copyright 2018 John Grosh <john.a.grosh@gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jagrosh.jmusicbot.commands.dj;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.audio.AudioHandler;
import com.jagrosh.jmusicbot.audio.FilterManager;
import com.jagrosh.jmusicbot.commands.DJCommand;
import com.jagrosh.jmusicbot.settings.Settings;

/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class KaraokeCmd extends DJCommand
{
    public KaraokeCmd(Bot bot)
    {
        super(bot);
        this.name = "karaoke";
        this.help = "enables/disables karaoke filter";
        this.arguments = "[on|off]";
        this.guildOnly = true;
    }


    @Override
    public void doCommand(CommandEvent event) {
        AudioHandler handler = (AudioHandler)event.getGuild().getAudioManager().getSendingHandler();

        FilterManager manager = handler.getFilterManager();

        boolean isEnabled = manager.isKaraokeEnabled();
        Settings settings = event.getClient().getSettingsFor(event.getGuild());
        if(event.getArgs().isEmpty())
        {
            event.reply("\ud83c\udfa4 Karaoke mode is currently `" + (isEnabled ? "enabled" : "disabled") + "`"); //🎤
            return;
        }
        else if(event.getArgs().equalsIgnoreCase("true") || event.getArgs().equalsIgnoreCase("on"))
        {
            isEnabled = true;
        }
        else if(event.getArgs().equalsIgnoreCase("false") || event.getArgs().equalsIgnoreCase("off"))
        {
            isEnabled = false;
        }
        else
        {
            event.replyError("Valid options are `on` or `off` (or leave empty to check current state)");
            return;
        }
        manager.setKaraokeEnabled(isEnabled);
        handler.getPlayer().setFilterFactory(manager.getFactory());
        event.reply("\ud83c\udfa4 Karaoke mode is now `"+(isEnabled ? "enabled" : "disabled")+"`"); //🎤
    }
}
