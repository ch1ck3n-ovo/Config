package client.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

import client.main.modules.Module;
import client.main.settings.BooleanSetting;
import client.main.settings.ModeSetting;
import client.main.settings.NumberSetting;
import client.main.settings.Setting;
import net.minecraft.client.settings.KeyBinding;

public class FileManager {
	
	private static final Logger logger = LogManager.getLogger();
	private static Gson gson = new Gson();
	
	private static File SETTING_DIR;
	
	public void init() {
		SETTING_DIR = new File("render.txt");
		loadSettings();
	}
	
	public void saveSettings() {
		try {
			PrintWriter var9 = new PrintWriter(new FileWriter(this.SETTING_DIR));
			for(Module m: Client.getModule()) {
				var9.println(m.getName() + " Enabled " + String.valueOf(m.isToggled()));
				for(Setting s: m.settings) {
					if(s instanceof ModeSetting) 
						var9.println(m.getName() + " " + ((ModeSetting)s).name + " " + String.valueOf(((ModeSetting)s).get()));
					else if(s instanceof BooleanSetting) 
						var9.println(m.getName() + " " + ((BooleanSetting)s).name + " " + String.valueOf(((BooleanSetting)s).get()));
					else if(s instanceof NumberSetting) 
						var9.println(m.getName() + " " + ((NumberSetting)s).name + " " + String.valueOf(((NumberSetting)s).get()));
				}
				var9.println("/");
			}
			var9.close();
		}catch(Exception e) {
			logger.error("Failed to save Render options", e);
		}
	}
	
	public void loadSettings() {
		try{
			if (!this.SETTING_DIR.exists()) return;

			BufferedReader var9 = new BufferedReader(new FileReader(this.SETTING_DIR));
			String var2 = "";

			while ((var2 = var9.readLine()) != null){
				try{
					if(var2.equals("/")) continue;
					String[] var8 = var2.split(" ");

					for(Module m: Client.getModule()) {
						if (m.getName().equals(var8[0])) {
							if(var8[1].equals("Enabled")) m.toggled = var8[2].equals("true");
							else {
								for(Setting s: m.settings) {
									if(var8[1].equals(s.name)) {
										if(s instanceof ModeSetting) ((ModeSetting)s).index = ((ModeSetting)s).modes.indexOf(var8[2]);
										else if(s instanceof BooleanSetting) ((BooleanSetting)s).setEnabled(var8[2].equals("true"));
										else if(s instanceof NumberSetting) ((NumberSetting)s).setValue(Double.valueOf(var8[2]));
									}
								}
							}
						}
					}
		                }catch (Exception var101) {
		                    logger.warn("Skipping bad option: " + var2);
		                    var101.printStackTrace();
		                }
		        }
		        KeyBinding.resetKeyBindingArrayAndHash();
		        var9.close();
	        }catch (Exception var111) {
	            logger.error("Failed to load Render options", var111);
	        }
	}
}
