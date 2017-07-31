package com.winter.horobot.command.impl.animals.wolf;

import com.winter.horobot.animals.Inventory;
import com.winter.horobot.animals.Item;
import com.winter.horobot.command.proccessing.Command;
import com.winter.horobot.command.proccessing.CommandType;
import com.winter.horobot.database.DataBase;
import com.winter.horobot.scheduler.HoroTask;
import com.winter.horobot.util.Cooldowns;
import com.winter.horobot.util.Localisation;
import com.winter.horobot.util.Message;
import com.winter.horobot.util.Utility;
import com.winter.horobot.animals.wolf.WolfCosmetics;
import com.winter.horobot.animals.wolf.WolfProfileBuilder;
import com.winter.horobot.animals.wolf.WolfTemplate;
import org.apache.commons.lang3.text.WordUtils;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.EmbedBuilder;

import java.awt.*;
import java.io.*;
import java.util.Arrays;
import java.util.stream.Collectors;

public class CommandWolf implements Command {

	@Override
	public boolean called(String[] args, MessageReceivedEvent event) {
		return true;
	}

	@Override
	public CommandType getType() {
		return CommandType.ANIMAL;
	}

	@Override
	public void action(String[] args, String raw, MessageReceivedEvent event) {
		if (args.length == 0) {
			DataBase.insertUser(event.getAuthor());
			DataBase.insertWolf(event.getAuthor());
			if(!Cooldowns.onCooldown("wolf-stats-" + event.getAuthor().getStringID(), event.getAuthor())) {
				Cooldowns.putOnCooldown("wolf-stats-" + event.getAuthor().getStringID(), event.getAuthor(), 10000);
				DataBase.insertWolf(event.getAuthor());
				try {
					Message.sendFile(
							event.getChannel(),
							WolfProfileBuilder.generateEmbed(event.getAuthor()),
							"Here's your wolf",
							"wolf.png",
							new ByteArrayInputStream(
									WolfProfileBuilder.generateProfile(
											event.getAuthor())));
				} catch (Exception e) {
					Message.sendMessageInChannel(event.getChannel(), "wolf-wrong-config");
				}
			} else {
				Message.sendMessageInChannel(event.getChannel(), "on-cooldown", Utility.formatTime(Cooldowns.getRemaining("wolf-stats-" + event.getAuthor().getStringID(), event.getAuthor())));
			}
		} else if (args.length >= 1) {
			if (event.getMessage().getMentions().size() == 1) {
				if(!event.getMessage().getMentions().get(0).isBot()) {
					if (!Cooldowns.onCooldown("wolf-stats-" + event.getAuthor().getStringID(), event.getAuthor())) {
						Cooldowns.putOnCooldown("wolf-stats-" + event.getAuthor().getStringID(), event.getAuthor(), 10000);
						DataBase.insertWolf(event.getMessage().getMentions().get(0));
						try {
							Message.sendFile(
									event.getChannel(),
									WolfProfileBuilder.generateEmbed(event.getMessage().getMentions().get(0)),
									"Here's " + event.getMessage().getMentions().get(0).getName() + "'s wolf",
									"wolf.png",
									new ByteArrayInputStream(
											WolfProfileBuilder.generateProfile(
													event.getMessage().getMentions().get(0))));
						} catch (Exception e) {
							Message.sendMessageInChannel(event.getChannel(), "wolf-wrong-config");
						}
					} else {
						Message.sendMessageInChannel(event.getChannel(), "on-cooldown", Utility.formatTime(Cooldowns.getRemaining("wolf-stats-" + event.getAuthor().getStringID(), event.getAuthor())));
					}
				} else {
					Message.sendMessageInChannel(event.getChannel(), "bot-user");
				}
			} else {
				switch (args[0]) {
					case "feed":
						if (args.length == 2) {
							WolfCosmetics.foods food = null;
							try {
								food = WolfCosmetics.foods.valueOf(args[1].toUpperCase());
							} catch (Exception e) {
								e.printStackTrace();
							}
							if (food != null) {
								if (!Cooldowns.onCooldown("wolf-feed-" + event.getAuthor().getStringID(), event.getAuthor())) {
									Cooldowns.putOnCooldown("wolf-feed-" + event.getAuthor().getStringID(), event.getAuthor(), food.getCooldown());
									if (DataBase.queryUser(event.getAuthor()).getNotifications()) {
										new HoroTask(event.getAuthor().getStringID() + "-note") {
											@Override
											public void run() {
												Message.sendPM(event.getAuthor(), "wolf-ready");
											}
										}.delay(food.getCooldown());
									}
									WolfTemplate template = DataBase.wolfQuery(event.getAuthor());
									if (template == null) {
										DataBase.insertWolf(event.getAuthor());
										template = DataBase.wolfQuery(event.getAuthor());
									}
									int hunger = template.getHunger();
									int maxHunger = template.getMaxHunger();
									int foodValue = food.getValue();
									hunger += foodValue;
									DataBase.updateWolf(event.getAuthor(), "hunger", hunger);
									DataBase.updateWolf(event.getAuthor(), "fedTimes", template.getFedTimes() + 1);

									StringBuilder message = new StringBuilder(String.format(Localisation.getMessage(event.getGuild().getStringID(), "feed-wolf") + "\n", WordUtils.capitalizeFully(food.getName())));
									if (hunger >= maxHunger) {
										int nextHunger = 1 + template.getLevel();
										DataBase.updateWolf(event.getAuthor(), "hunger", 0);
										DataBase.updateWolf(event.getAuthor(), "maxHunger", nextHunger);
										DataBase.updateWolf(event.getAuthor(), "level", template.getLevel() + 1);
										message.append(String.format("**LEVEL UP!** Your wolf is now level **%s**!\n", template.getLevel() + 1));
									}

									DataBase.updateUser(event.getAuthor(), "foxCoins", (DataBase.queryUser(event.getAuthor()).getFoxCoins() + 100));
									message.append("**+100** Coins for feeding your wolf!\n");

									Message.sendFile(
											event.getChannel(),
											WolfProfileBuilder.generateEmbed(event.getAuthor()),
											message.toString(),
											"wolf.png",
											new ByteArrayInputStream(
													WolfProfileBuilder.generateProfile(
															event.getAuthor())));
								} else {
									Message.sendMessageInChannel(event.getChannel(), "wolf-full", Utility.formatTime(Cooldowns.getRemaining("wolf-feed-" + event.getAuthor().getStringID(), event.getAuthor())));
								}
							} else {
								Message.sendMessageInChannel(event.getChannel(), "no-food");
							}
						} else {
							Message.sendMessageInChannel(event.getChannel(), "feed-help");
						}
						break;
					case "rename": {
						String temp = Arrays.stream(args).skip(1).collect(Collectors.joining(" "));
						if (temp.length() <= 18) {
							DataBase.updateWolf(event.getAuthor(), "name", temp);
							Message.sendMessageInChannel(event.getChannel(), "name-success", temp);
						} else {
							Message.sendMessageInChannel(event.getChannel(), "name-too-long");
						}
						break;
					}
					case "equip": {
						String temp = Arrays.stream(args).skip(1).collect(Collectors.joining(" "));
						Item item = DataBase.queryItem(event.getAuthor(), temp.toUpperCase());
						if (item != null) {
							DataBase.updateWolf(event.getAuthor(), Utility.getItemStringType(item), item.getName().toUpperCase());
							Message.sendMessageInChannel(event.getChannel(), "background-success", WordUtils.capitalizeFully(temp));
						} else {
							Message.sendMessageInChannel(event.getChannel(), "no-item");
						}
						break;
					}
					case "strip": {
						DataBase.updateWolf(event.getAuthor(), "background", "NONE0");
						DataBase.updateWolf(event.getAuthor(), "hat", "NONE1");
						DataBase.updateWolf(event.getAuthor(), "body", "NONE2");
						DataBase.updateWolf(event.getAuthor(), "paws", "NONE3");
						DataBase.updateWolf(event.getAuthor(), "tail", "NONE4");
						DataBase.updateWolf(event.getAuthor(), "shirt", "NONE5");
						DataBase.updateWolf(event.getAuthor(), "nose", "NONE6");
						DataBase.updateWolf(event.getAuthor(), "eye", "NONE7");
						DataBase.updateWolf(event.getAuthor(), "neck", "NONE8");
						Message.sendMessageInChannel(event.getChannel(), "stripped-items");
						break;
					}
					case "inventory": {
						/*ArrayList<String> items = (ArrayList<String>) DataBase.queryItems(event.getAuthor());
						Message.sendRawMessageInChannel(
								event.getChannel(),
								"Here's your inventory!\n" +
										"```\n" +
										Utility.listAsString(items) + "\n" +
										"```");*/
						Inventory inventory = Utility.assembleInventory(event.getAuthor());
						EmbedBuilder builder = new EmbedBuilder();
						builder.withColor(Color.CYAN);
						builder.withAuthorIcon(Utility.getAvatar(event.getAuthor()));
						builder.withAuthorName(event.getAuthor().getName() + "'s Inventory");
						if (inventory != null) {
							if (Utility.hasAllItems(event.getAuthor()))
								builder.appendField("Yep, you got them all!", "You currently own all available items!", false);
							else
								builder.appendField("Nope, not there yet!", "You don't own all items yet, keep collecting!", false);
							builder.appendField("Backgrounds", Utility.itemsAsString(inventory.getBackgrounds()), false);
							builder.appendField("Hats", Utility.itemsAsString(inventory.getHats()), false);
							builder.appendField("Bodies", Utility.itemsAsString(inventory.getBodies()), false);
							builder.appendField("Paws", Utility.itemsAsString(inventory.getPaws()), false);
							builder.appendField("Tails", Utility.itemsAsString(inventory.getTails()), false);
							builder.appendField("Shirts", Utility.itemsAsString(inventory.getShirts()), false);
							builder.appendField("Noses", Utility.itemsAsString(inventory.getNoses()), false);
							builder.appendField("Eyes", Utility.itemsAsString(inventory.getEyes()), false);
							builder.appendField("Neck", Utility.itemsAsString(inventory.getNeck()), false);
						} else {
							builder.withDescription(Localisation.getMessage(event.getGuild().getStringID(), "inventory-empty"));
						}
						Message.sendEmbed(event.getChannel(), "Here's your inventory", builder.build(), false);
						break;
					}
					case "capsule": {
						if (DataBase.queryUser(event.getAuthor()).getFoxCoins() >= 200) {
							Item drop = Utility.dropItem(event.getAuthor());
							if (drop != null) {
								DataBase.updateUser(event.getAuthor(), "foxCoins", (DataBase.queryUser(event.getAuthor()).getFoxCoins() - 200));
								DataBase.insertItem(event.getAuthor(), drop.getName().toUpperCase());
								InputStream stream = getClass().getResourceAsStream(drop.getFile());
								if (stream == null) return;
								try {
									File temp = File.createTempFile(String.valueOf(stream.hashCode()), ".png");
									temp.deleteOnExit();

									try (FileOutputStream out = new FileOutputStream(temp)) {
										byte[] buffer = new byte[1024];
										int bytesRead;
										while((bytesRead = stream.read(buffer)) != -1) {
											out.write(buffer, 0, bytesRead);
										}
									}
									Message.sendFile(
											event.getChannel(),
											String.format(Localisation.getMessage(event.getGuild().getStringID(), "capsule-opened"), WordUtils.capitalizeFully(drop.getName().toLowerCase())),
											temp);
								} catch (IOException e) {
									Message.sendMessageInChannel(event.getChannel(), "html-server-error");
									e.printStackTrace();
								}
							} else {
								Message.sendMessageInChannel(event.getChannel(), "got-everything");
							}
						} else {
							Message.sendMessageInChannel(event.getChannel(), "insufficient-funds");
						}
						//Message.sendRawMessageInChannel(event.getChannel(), "This command is currently broken and under construction");
						break;
					}
					case "notifications": {
						if(DataBase.queryUser(event.getAuthor()).getNotifications()) {
							DataBase.updateUser(event.getAuthor(), "notifications", false);
							Message.sendMessageInChannel(event.getChannel(), "notifications-off");
						} else {
							DataBase.updateUser(event.getAuthor(), "notifications", true);
							Message.sendMessageInChannel(event.getChannel(), "notifications-on");
						}
						break;
					}
					case "foods": {
						Message.sendEmbed(event.getChannel(), "", Utility.getFoods(), false);
						break;
					}
					case "help": {
						Message.sendMessageInChannel(event.getChannel(), help());
						break;
					}
					default: {
						Message.sendMessageInChannel(event.getChannel(), help());
						break;
					}
				}
			}
		} else {
			Message.sendMessageInChannel(event.getChannel(), help());
		}
	}

	@Override
	public String help() {
		return "wolf-help";
	}

	@Override
	public void executed(boolean success, MessageReceivedEvent event) {
		if (success)
			Utility.commandsExecuted++;
	}
}