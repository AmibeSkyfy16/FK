package ch.skyfy.fk;


import ch.skyfy.fk.commands.*;
import ch.skyfy.fk.commands.featured.CaptureCmd;
import ch.skyfy.fk.commands.featured.GetMarkerCmd;
import ch.skyfy.fk.config.Configs;
import ch.skyfy.fk.exceptions.FKModException;
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

    public static final Logger LOGGER = LogManager.getLogger(MOD_ID.toUpperCase());

    public static final Path CONFIG_DIRECTORY = FabricLoader.getInstance().getConfigDir().resolve(MOD_ID);

    private boolean firstJoin = false;

    private final AtomicReference<Optional<FKGame>> optFKGameRef;

    private final StartCmd startCmd;
    private final PauseCmd pauseCmd;
    private final ResumeCmd resumeCmd;
    private final WhereIAmCmd whereIAmCmd;
    private final SetFKTimeCmd setFKTimeCmd;
    private final GetMarkerCmd getMarkerCmd;
    private final CaptureCmd captureCmd;

    public FKMod() throws Exception {
        // Create a config directory named with the MOD_ID under config folder of the server
        createConfigDirectory();

        // Load Configs.class a class that contains all our configuration data class
        ReflectionUtils.loadClassesByReflection(new Class[]{Configs.class});

        optFKGameRef = new AtomicReference<>(Optional.empty());

        startCmd = new StartCmd(optFKGameRef);
        pauseCmd = new PauseCmd(optFKGameRef);
        resumeCmd = new ResumeCmd(optFKGameRef);
        whereIAmCmd = new WhereIAmCmd();
        setFKTimeCmd = new SetFKTimeCmd(optFKGameRef);
        getMarkerCmd = new GetMarkerCmd();
        captureCmd = new CaptureCmd(optFKGameRef);
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
            startCmd.register(dispatcher);
            pauseCmd.register(dispatcher);
            resumeCmd.register(dispatcher);
            setFKTimeCmd.register(dispatcher);
            getMarkerCmd.register(dispatcher);
            captureCmd.register(dispatcher);
            whereIAmCmd.register(dispatcher);
        });
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static void createConfigDirectory() {
        try {
            var file = CONFIG_DIRECTORY.toFile();
            if (!file.exists()) file.mkdir();
        } catch (UnsupportedOperationException | SecurityException e) {
            FKMod.LOGGER.fatal("Could not create the root folder that should contain the configuration files");
            throw new FKModException(e);
        }
    }

}
