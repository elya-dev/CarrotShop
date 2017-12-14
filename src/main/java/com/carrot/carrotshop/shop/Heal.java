package com.carrot.carrotshop.shop;

import java.math.BigDecimal;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.carrot.carrotshop.CarrotShop;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class Heal extends Shop {
	@Setting
	private float price;

	public Heal() {
	}

	public Heal(Player player, Location<World> sign) throws ExceptionInInitializerError {
		super(sign);
		if (!player.hasPermission("carrotshop.admin.heal"))
			throw new ExceptionInInitializerError("You don't have perms to build a heal sign");

		if (CarrotShop.getEcoService() != null) {
			price = getPrice(sign);
			if (price < 0)
				throw new ExceptionInInitializerError("bad price");
		}
		player.sendMessage(Text.of(TextColors.DARK_GREEN, "You have setup a heal sign"));
		done(player);
	}

	@Override
	public void info(Player player) {
		if (CarrotShop.getEcoService() != null)
			player.sendMessage(Text.of("Heal for ", formatPrice(price), "?"));
		else
			player.sendMessage(Text.of("Heal?"));
		update();

	}

	@Override
	public boolean trigger(Player player) {
		if (CarrotShop.getEcoService() != null) {
			UniqueAccount buyerAccount = CarrotShop.getEcoService().getOrCreateAccount(player.getUniqueId()).get();
			TransactionResult result = buyerAccount.withdraw(getCurrency(), BigDecimal.valueOf(price), Cause.source(this).build());
			if (result.getResult() != ResultType.SUCCESS) {
				player.sendMessage(Text.of(TextColors.DARK_RED, "You don't have enough money!"));
				return false;
			}
			player.sendMessage(Text.of("You healed for ", formatPrice(price)));
		}
		else
			player.sendMessage(Text.of("You healed"));

		player.offer(Keys.HEALTH, player.get(Keys.MAX_HEALTH).get());

		return true;
	}

}
