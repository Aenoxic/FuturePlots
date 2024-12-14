/*
 * Copyright 2022 KCodeYT
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Modified 2024 by tim03we, Ovis Development
 */

package ovis.futureplots.commands.sub;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import ovis.futureplots.FuturePlots;
import ovis.futureplots.commands.SubCommand;
import ovis.futureplots.event.PlotClaimEvent;
import ovis.futureplots.event.PlotPreClaimEvent;
import ovis.futureplots.components.util.language.TranslationKey;
import ovis.futureplots.manager.PlotManager;
import ovis.futureplots.components.util.Plot;

/**
 * @modified Tim tim03we, Ovis Development (2024)
 */
public class ClaimCommand extends SubCommand {

    private final FuturePlots plugin;

    public ClaimCommand(FuturePlots plugin) {
        super(plugin, "claim");
        this.plugin = plugin;
        this.identify();
        this.playerOnly();
        this.setPermissions("plots.claim", "plots.perm.basic");
    }

    @Override
    public void execute(CommandSender sender, String command, String[] args) {
        Player player = (Player) sender;
        final PlotManager plotManager = this.plugin.getPlotManager(player.getLevel());
        final Plot plot;
        if(plotManager == null || (plot = plotManager.getMergedPlot(player.getFloorX(), player.getFloorZ())) == null) {
            player.sendMessage(this.translate(player, TranslationKey.NO_PLOT));
            return;
        }

        final int ownedPlots = plotManager.getPlotsByOwner(player.getUniqueId()).size();
        if(!player.hasPermission("plot.limit.unlimited")) {
            int maxLimit = -1;
            for(String permission : player.getEffectivePermissions().keySet()) {
                if(permission.startsWith("plot.limit.")) {
                    try {
                        final String limitStr = permission.substring("plot.limit.".length());
                        if(limitStr.isBlank()) continue;
                        final int limit = Integer.parseInt(limitStr);

                        if(limit > maxLimit) maxLimit = limit;
                    } catch(NumberFormatException ignored) {
                    }
                }
            }

            if(maxLimit > 0 && ownedPlots >= maxLimit) {
                player.sendMessage(this.translate(player, TranslationKey.CLAIM_FAILURE_TOO_MANY, ownedPlots));
                return;
            }
        }

        if(plot.hasOwner()) {
            player.sendMessage(this.translate(player, TranslationKey.CLAIM_FAILURE));
            return;
        }

        final PlotPreClaimEvent plotPreClaimEvent = new PlotPreClaimEvent(player, plot, false, true, true);
        this.plugin.getServer().getPluginManager().callEvent(plotPreClaimEvent);

        if(plotPreClaimEvent.isCancelled()) {
            if(plotPreClaimEvent.isShowCancelMessage())
                player.sendMessage(this.translate(player, TranslationKey.CLAIM_FAILURE));
            return;
        }

        plot.setOwner(player.getUniqueId());
        if(plotPreClaimEvent.isBorderChanging())
            plotManager.changeBorder(plot, plotManager.getLevelSettings().getClaimPlotState());
        plotManager.savePlot(plot);

        final PlotClaimEvent plotClaimEvent = new PlotClaimEvent(player, plot, false);
        this.plugin.getServer().getPluginManager().callEvent(plotClaimEvent);

        if(FuturePlots.getSettings().isTeleportOnClaim()) {
            plotManager.teleportPlayerToPlot(player, plot, false);
        }

        player.sendMessage(this.translate(player, TranslationKey.CLAIM_SUCCESS));
        return;
    }

}
