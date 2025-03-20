package com.earth2me.essentials.commands;

import com.earth2me.essentials.Trade;
import com.earth2me.essentials.User;
import com.earth2me.essentials.craftbukkit.Inventories;
import com.earth2me.essentials.utils.AdventureUtil;
import com.earth2me.essentials.utils.NumberUtil;
import net.ess3.api.TranslatableException;
import net.ess3.api.events.UserBalanceUpdateEvent;
import org.bukkit.Server;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.logging.Level;

import static com.earth2me.essentials.I18n.tlLiteral;

public class Commandbuy extends EssentialsCommand {
    public Commandbuy() {
        super("buy");
    }

    @Override
    protected void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        if (args.length == 0) {
            throw new NotEnoughArgumentsException();
        }

        final ItemStack itemToBuy = ess.getItemDb().get(args[0]);

        // Check if item can be bought on server
        final BigDecimal cost = ess.getWorth().getPrice(ess, itemToBuy);
        if (cost == null) {
            throw new TranslatableException("itemCannotBeBought");
        }

        int amount = 1;
        if (args.length > 1) {
            try {
                amount = Integer.parseInt(args[1].replaceAll("[^0-9]", ""));
            } catch (final NumberFormatException ex) {
                throw new NotEnoughArgumentsException(ex);
            }
        }

        if (amount < 1) {
            // TODO: Proper messaging for negative or zero items
            throw new NotEnoughArgumentsException();
        }

        final int maxStackSize = user.isAuthorized("essentials.oversizedstacks") ? ess.getSettings().getOversizedStackSize() : itemToBuy.getMaxStackSize();
        final int inventorySpace = Inventories.getSpaceForItem(user.getBase(), maxStackSize, itemToBuy);

        // Lower amount bought to fit in inventory
        amount = Math.min(amount, inventorySpace);

        final BigDecimal totalCost = cost.multiply(new BigDecimal(amount));
        if (!user.canAfford(totalCost)) {
            throw new TranslatableException("notEnoughMoney");
        }

        itemToBuy.setAmount(amount);
        Inventories.addItem(user.getBase(), maxStackSize, itemToBuy);
        user.getBase().updateInventory();
        Trade.log("Command", "Buy", "Item", user.getName(), new Trade(totalCost, ess), user.getName(), new Trade(itemToBuy, ess), user.getLocation(), user.getMoney(), ess);
        user.takeMoney(totalCost, null, UserBalanceUpdateEvent.Cause.COMMAND_BUY);
        final String typeName = itemToBuy.getType().toString().toLowerCase(Locale.ENGLISH);
        final AdventureUtil.ParsedPlaceholder worthDisplay = AdventureUtil.parsed(NumberUtil.displayCurrency(cost, ess));
        user.sendTl("itemBought", AdventureUtil.parsed(NumberUtil.displayCurrency(totalCost, ess)), amount, typeName, worthDisplay);
        ess.getLogger().log(Level.INFO, AdventureUtil.miniToLegacy(tlLiteral("itemBoughtConsole", user.getName(), typeName, AdventureUtil.miniToLegacy(NumberUtil.displayCurrency(totalCost, ess)), amount, AdventureUtil.miniToLegacy(worthDisplay.toString()), user.getDisplayName())));
    }
}
