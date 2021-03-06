package net.fe.builderStage;

import java.util.ArrayList;
import java.util.HashMap;

import net.fe.FEMultiplayer;
import net.fe.Player;
import net.fe.Session;
import net.fe.network.FEServer;
import net.fe.network.Message;
import net.fe.network.message.PartyMessage;
import net.fe.network.message.QuitMessage;
import net.fe.network.message.StartGame;
import net.fe.overworldStage.OverworldStage;
import net.fe.unit.Unit;
import chu.engine.Game;
import chu.engine.Stage;

/**
 * Wait for all players to select
 * @author Shawn
 *
 */
public class WaitStage extends Stage {
	
	private HashMap<Byte, Boolean> readyStatus;
	private ArrayList<PartyMessage> messages;
	private boolean sentStartMessage;
	protected Session session;
	
	public WaitStage(Session s) {
		super("preparations");
		session = s;
		init();
	}
	
	protected void init() {
		readyStatus = new HashMap<Byte, Boolean>();
		sentStartMessage = false;
		for(Player p : session.getPlayers()) {
			if(!p.isSpectator()) readyStatus.put(p.getID(), false);
		}
		messages = new ArrayList<PartyMessage>();
	}

	@Override
	public void beginStep() {
		for(Message message : Game.getMessages()) {
			if(message instanceof PartyMessage) {
				PartyMessage pm = (PartyMessage)message;
				for(Player p : session.getPlayers()){ 
					if(p.getID() == message.origin) {
						p.getParty().clear();
						for(Unit u : pm.teamData)
							p.getParty().addUnit(u);
						readyStatus.put(p.getID(), true);
					}
				}
				messages.add(pm);
			}
			else if(message instanceof QuitMessage) {
				//player has left
				FEMultiplayer.disconnectGame("Opponent has disconnected. Exiting game.");
			}
		}
		
	}

	@Override
	public void onStep() {
		
	}

	@Override
	public void endStep() {
		if(!sentStartMessage) {
			for(boolean b : readyStatus.values()) {
				if(!b) return;
			}
			for(PartyMessage pm : messages) {
				FEServer.getServer().broadcastMessage(pm);
			}
			FEServer.getServer().broadcastMessage(new StartGame((byte) 0));
			for(Player p : session.getPlayers()) {
				for(Unit u : p.getParty()) {
					u.initializeEquipment();
				}
			}
			FEServer.setCurrentStage(new OverworldStage(session));
			sentStartMessage = true;
		}
	}
	
}
