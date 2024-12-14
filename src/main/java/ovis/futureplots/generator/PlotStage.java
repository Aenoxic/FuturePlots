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

package ovis.futureplots.generator;

import cn.nukkit.level.Level;
import cn.nukkit.level.format.ChunkState;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.format.LevelProvider;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.GenerateStage;
import ovis.futureplots.FuturePlots;
import ovis.futureplots.manager.PlotManager;
import ovis.futureplots.schematic.Schematic;
import ovis.futureplots.components.util.ShapeType;

import static ovis.futureplots.generator.PlotGenerator.*;

/**
 * @modified Tim tim03we, Ovis Development (2024)
 */
public class PlotStage extends GenerateStage {
    public static final String NAME = "plot_gen";

    @Override
    public void apply(ChunkGenerateContext chunkGenerateContext) {
        final IChunk chunk = chunkGenerateContext.getChunk();
        LevelProvider provider = chunk.getProvider();
        Level level = provider.getLevel();
        if (level == null) return;
        final PlotManager plotManager = FuturePlots.INSTANCE.getPlotManager(level);
        if (plotManager == null) return;
        final ShapeType[] shapes = plotManager.getShapes(chunk.getX() << 4, chunk.getZ() << 4);

        preGenerateChunk(plotManager, chunk, shapes, GENERATE_ALLOWED, true, null, null, null, null);
        final Schematic schematic = plotManager.getPlotSchematic().getSchematic();
        if (schematic != null)
            placeChunkSchematic(plotManager, schematic, chunk, shapes, GENERATE_ALLOWED, null, null, null, null);
        chunk.setChunkState(ChunkState.POPULATED);
    }

    @Override
    public String name() {
        return NAME;
    }
}
