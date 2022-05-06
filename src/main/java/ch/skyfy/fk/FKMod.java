package ch.skyfy.fk;


import ch.skyfy.fk.commands.*;
import ch.skyfy.fk.config.Configs;
import ch.skyfy.fk.logic.FKGame;
import ch.skyfy.fk.utils.ReflectionUtils;
import me.bymartrixx.playerevents.api.event.PlayerJoinCallback;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class FKMod implements DedicatedServerModInitializer {

    public enum GameState {
        NOT_STARTED,
        RUNNING,
        PAUSED
    }

    public static final String MOD_ID = "fk";

    public static final Logger LOGGER = LogManager.getLogger();

    public static final Path CONFIG_DIRECTORY = FabricLoader.getInstance().getConfigDir().resolve(MOD_ID);

    private boolean firstJoin = false;

    private final AtomicReference<Optional<FKGame>> optFKGameRef;

    private final StartCmd startCmd;
    private final PauseCmd pauseCmd;
    private final ResumeCmd resumeCmd;

    private final SetFKTime setFKTime;

    public FKMod() throws Exception {
        // Create a config directory named with the MOD_ID under config folder of the server
        createConfigDirectory();

        // Load Configs.class a class that contains all our configuration data class
        ReflectionUtils.loadConfigByReflection(new Class[]{Configs.class});

        optFKGameRef = new AtomicReference<>(Optional.empty());

        startCmd = new StartCmd(optFKGameRef);
        pauseCmd = new PauseCmd(optFKGameRef);
        resumeCmd = new ResumeCmd(optFKGameRef);
        setFKTime = new SetFKTime(optFKGameRef);

    }

    @Override
    public void onInitializeServer() {
        PlayerJoinCallback.EVENT.register(this::onFirstPlayerJoin);
        registerCommands();
    }

    private void onFirstPlayerJoin(ServerPlayerEntity player, MinecraftServer server) {
        if (firstJoin) return;
        if (server.getPlayerManager().getPlayerList().size() == 1) {
            firstJoin = true;
            final var fkGame = new FKGame(server, player);
            optFKGameRef.set(Optional.of(fkGame));
        }
    }

    public void registerCommands() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            setFKTime.register(dispatcher, dedicated);
            dispatcher.register(net.minecraft.server.command.CommandManager.literal("FKStart").executes(startCmd));
            dispatcher.register(net.minecraft.server.command.CommandManager.literal("FKPause").executes(pauseCmd));
            dispatcher.register(net.minecraft.server.command.CommandManager.literal("FKResume").executes(resumeCmd));

            dispatcher.register(net.minecraft.server.command.CommandManager.literal("WhereIAm").executes(new WhereIAmCmd()));
        });
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static void createConfigDirectory() {
        try {
            var file = CONFIG_DIRECTORY.toFile();
            if (!file.exists()) file.mkdir();
        } catch (UnsupportedOperationException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }

}
