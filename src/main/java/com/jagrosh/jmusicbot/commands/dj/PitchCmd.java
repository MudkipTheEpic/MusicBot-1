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

/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class PitchCmd extends DJCommand
{
    public PitchCmd(Bot bot)
    {
        super(bot);
        this.name = "pitch";
        this.aliases = new String[]{};
        this.help = "Adjusts playback pitch";
        this.arguments = "[25-400] or [reset]";
    }

    @Override
    public void doCommand(CommandEvent event)
    {

        AudioHandler handler = (AudioHandler)event.getGuild().getAudioManager().getSendingHandler();

        FilterManager manager = handler.getFilterManager();

        double pitchScale = manager.getPitchScaleFactor();

        if(event.getArgs().isEmpty())
        {
            event.reply("\ud83d\udce3 Current pitch value is `"+(int) (pitchScale * 100)+"`%");
        }
        else
        {
            double newPitchScale;
            try{
                newPitchScale = ((double) Integer.parseInt(event.getArgs()) / 100);
            }catch(NumberFormatException e) {
                if (event.getArgs().equalsIgnoreCase("reset")) {
                    newPitchScale = 1;
                } else {
                    newPitchScale = -1;
                }
            }
            if(!manager.setPitchScaleFactor(newPitchScale))
                event.reply(event.getClient().getError()+" Pitch value must be a valid integer between 25 and 400!");
            else
            {
                handler.getPlayer().setFilterFactory(manager.getFactory());
                event.reply("\ud83d\udce3 Pitch value changed from `"+(int) (pitchScale*100)+"`% to `"+(int) (newPitchScale*100)+"`%");
            }
        }
    }
}
