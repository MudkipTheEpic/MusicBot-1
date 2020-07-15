/*
 * Copyright 2016 John Grosh <john.a.grosh@gmail.com>.
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
import com.jagrosh.jmusicbot.utils.FormatUtil;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;

/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class BassCmd extends DJCommand
{
    public BassCmd(Bot bot)
    {
        super(bot);
        this.name = "bass";
        this.aliases = new String[]{"boost"};
        this.help = "Adjusts bass levels";
        this.arguments = "[1-500] or [reset]";
    }

    @Override
    public void doCommand(CommandEvent event)
    {

        AudioHandler handler = (AudioHandler)event.getGuild().getAudioManager().getSendingHandler();

        FilterManager manager = handler.getFilterManager();

        float bassLevel = manager.getBassScaleFactor();

        if(event.getArgs().isEmpty())
        {
            event.reply(FormatUtil.volumeIcon((int) (Math.sqrt(bassLevel) * 50 ))+" Current bass level is `"+(int) (bassLevel * 100)+"`%");
        }
        else
        {
            float newBassLevel;
            try{
                newBassLevel = ((float) Integer.parseInt(event.getArgs()) / 100);
            }catch(NumberFormatException e){
                if (event.getArgs().equalsIgnoreCase("reset")) {
                    newBassLevel = 1;
                } else {
                    newBassLevel = -1;
                }
            }
            if(!manager.setBassScaleFactor(newBassLevel))
                event.reply(event.getClient().getError()+" Bass level must be a valid integer between 0 and 500!");
            else
            {
                handler.getPlayer().setFilterFactory(manager.getFactory());
                event.reply(FormatUtil.volumeIcon((int) (Math.sqrt(newBassLevel) * 50 ))+" Bass level changed from `"+(int) (bassLevel*100)+"`% to `"+(int) (newBassLevel*100)+"`%");
            }
        }
    }

}
