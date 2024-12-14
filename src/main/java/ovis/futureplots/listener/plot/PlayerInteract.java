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

package ovis.futureplots.listener.plot;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.item.Item;
import lombok.RequiredArgsConstructor;
import ovis.futureplots.FuturePlots;
import ovis.futureplots.components.util.language.Language;
import ovis.futureplots.components.util.language.TranslationKey;
import ovis.futureplots.manager.PlotManager;
import ovis.futureplots.components.util.Plot;
import ovis.futureplots.components.util.Utils;

import java.util.List;

/**
 * @modified Tim tim03we, Ovis Development (2024)
 */
@RequiredArgsConstructor
public class PlayerInteract implements Listener {

    private final FuturePlots plugin;

    @EventHandler
    public void on(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final PlotManager plotManager = this.plugin.getPlotManager(player.getLevel());

        if(plotManager != null && !player.hasPermission("plot.admin.interact")) {
            final Block block = event.getBlock();
            final Item item = event.getItem();

            if(event.getAction() == PlayerInteractEvent.Action.PHYSICAL && block != null) {
                final Plot plot = plotManager.getMergedPlot(block.getFloorX(), block.getFloorZ());

                if(plot != null) {
                    if(!plot.isOwner(player.getUniqueId()) && !plot.isHelper(player.getUniqueId()) && !plot.isHelper(Utils.UUID_EVERYONE))
                        event.setCancelled(true);
                }

                return;
            }

            if((block != null && ((!player.isSneaking() || item == null || item.isNull()) && block.canBeActivated())) || (item != null && item.canBeActivated())) {
                final int x = (block == null || block.isAir() ? player : block).getFloorX();
                final int z = (block == null || block.isAir() ? player : block).getFloorZ();
                final Plot plot = plotManager.getMergedPlot(x, z);

                if(plot != null) {
                    if(!plot.isOwner(player.getUniqueId()) && !plot.isHelper(player.getUniqueId()) && !plot.isHelper(Utils.UUID_EVERYONE))
                        event.setCancelled(true);

                    if(plot.getHomePosition() != null && plot.getHomePosition().distance(event.getBlock()) < 5) {
                        event.setCancelled(true);
                        Language language = new Language(player.getLoginChainData().getLanguageCode());
                        player.sendMessage(language.message(TranslationKey.TOO_CLOSE_TO_HOME));
                    }
                } else {
                    event.setCancelled(true);
                }
            }
        }
    }
}