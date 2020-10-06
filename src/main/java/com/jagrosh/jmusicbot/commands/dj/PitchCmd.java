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
    public void doCommand(CommandEvent event) {

        AudioHandler handler = (AudioHandler)event.getGuild().getAudioManager().getSendingHandler();

        FilterManager manager = handler.getFilterManager();

		double pitchScale = manager.getPitchScaleFactor();
		
        if(event.getArgs().isEmpty()) {
            event.reply("\ud83d\udce3 Current pitch value is `"+(int) (pitchScale * 100)+"`%");
        } else {
			String input = event.getArgs();
            double newPitchScale;
            if (event.getArgs().equalsIgnoreCase("reset")) {
                    newPitchScale = 1;
			} else {
				newPitchScale = getNewPitch(event.getArgs(), pitchScale);
			}
            if(!manager.setPitchScaleFactor(newPitchScale)) {
				event.reply(event.getClient().getError()+" Pitch value must be a valid integer between 25 and 400!");
			}
            else
            {
                handler.getPlayer().setFilterFactory(manager.getFactory());
                event.reply("\ud83d\udce3 Pitch value changed from `"+(int) (pitchScale*100)+"`% to `"+(int) (newPitchScale*100)+"`%");
            }
        }
	}
	
	private double getNewPitch(String argument, double oldPitch) {
		try {
			int relative = 0;
			if (argument.startsWith("+")) {
					relative = 1;
					argument = argument.substring(1);
			} else if (argument.startsWith("-")) {
					relative = -1;
					argument = argument.substring(1);
			}
			if (argument.endsWith("o")) {
				double octaveAdjustment;
				if (relative != 0) {
					octaveAdjustment = Math.log(oldPitch) / 0.69314718056;
					octaveAdjustment += relative * Double.parseDouble(argument.substring(0, argument.length() - 1));
				} else { 
					octaveAdjustment = Double.parseDouble(argument.substring(0, argument.length() - 1));
				}
				return Math.exp(0.69314718056 * Math.max(Math.min(octaveAdjustment, 1.0), -1.0));
			} else if (argument.endsWith("s")) {
				double semitoneAdjustment;
				if (relative != 0) {
					semitoneAdjustment = (Math.log(oldPitch) / 0.69314718056) * 12;
					semitoneAdjustment += relative * Double.parseDouble(argument.substring(0,argument.length()-1));
				} else {
					semitoneAdjustment = Double.parseDouble(argument.substring(0, argument.length() - 1));
				}
				return Math.exp(0.69314718056 * Math.max(Math.min(semitoneAdjustment / 12, 1.0), -1.0));
			}
			if (relative != 0) {
				return oldPitch + (relative * Double.parseDouble(argument) / 100.0);
			}
			return Double.parseDouble(argument) / 100.0;
		} catch (NumberFormatException e) {
			throw e;
			//return -1;
		}
	}
}
