package jjben.asynchstatlogger.fwk.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import jjben.asynchstatlogger.fwk.actor.AsynchronousStatEngine;

public class Configuration {

    private static final Logger LOGGER = Logger.getLogger(AsynchronousStatEngine.class.getName());

    private Properties config;

    private String fileConfig;

    public Configuration(String fileConfig) {
	this.fileConfig = fileConfig;
    }

    public void initConfig() throws IOException {
	if (config == null) {
	    config = new Properties();
	    try {
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileConfig);
		config.load(is);
	    } catch (IOException e) {

		LOGGER.log(Level.WARNING, "Impossible de charger le fichier de config " + fileConfig);
		throw new IOException("Impossible de charger le fichier de config", e);

	    }
	}

    }

    public Integer getAggragationPeriodictyInSeconds() {
	return Integer.parseInt((String) config.get("aggragationPeriodictyInSeconds"));
    }

    public Integer getNbThreadConsummer() {
	return Integer.parseInt((String) config.get("nbThreadConsummer"));
    }

}
