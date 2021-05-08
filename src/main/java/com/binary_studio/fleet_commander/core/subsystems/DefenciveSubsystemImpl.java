package com.binary_studio.fleet_commander.core.subsystems;

import com.binary_studio.fleet_commander.core.actions.attack.AttackAction;
import com.binary_studio.fleet_commander.core.actions.defence.RegenerateAction;
import com.binary_studio.fleet_commander.core.common.PositiveInteger;
import com.binary_studio.fleet_commander.core.subsystems.contract.DefenciveSubsystem;

public final class DefenciveSubsystemImpl implements DefenciveSubsystem {

	private String name;

	private PositiveInteger powerGridConsumption;

	private PositiveInteger capacitorConsumption;

	private PositiveInteger impactReductionPercent;

	private PositiveInteger shieldRegeneration;

	private PositiveInteger hullRegeneration;

	public DefenciveSubsystemImpl(String name, PositiveInteger powerGridConsumption,
			PositiveInteger capacitorConsumption, PositiveInteger impactReductionPercent,
			PositiveInteger shieldRegeneration, PositiveInteger hullRegeneration) {
		this.name = name;
		this.powerGridConsumption = powerGridConsumption;
		this.capacitorConsumption = capacitorConsumption;
		this.impactReductionPercent = impactReductionPercent;
		this.shieldRegeneration = shieldRegeneration;
		this.hullRegeneration = hullRegeneration;
	}

	public static DefenciveSubsystemImpl construct(String name, PositiveInteger powerGridConsumption,
			PositiveInteger capacitorConsumption, PositiveInteger impactReductionPercent,
			PositiveInteger shieldRegeneration, PositiveInteger hullRegeneration) throws IllegalArgumentException {
		if (name == null || "".equals(name.trim())) {
			throw new IllegalArgumentException("Name should be not null and not empty");
		}

		return new DefenciveSubsystemImpl(name, powerGridConsumption, capacitorConsumption, impactReductionPercent,
				shieldRegeneration, hullRegeneration);
	}

	@Override
	public PositiveInteger getPowerGridConsumption() {
		return this.powerGridConsumption;
	}

	@Override
	public PositiveInteger getCapacitorConsumption() {
		return this.capacitorConsumption;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public AttackAction reduceDamage(AttackAction incomingDamage) {
		int reduceDamege = (int) Math.ceil(this.impactReductionPercent.value() <= 95
				? (1 - this.impactReductionPercent.value() * 0.01) * incomingDamage.damage.value()
				: 0.05 * incomingDamage.damage.value());
		return new AttackAction(PositiveInteger.of(reduceDamege), incomingDamage.attacker, incomingDamage.target,
				incomingDamage.weapon);
	}

	@Override
	public RegenerateAction regenerate() {
		return new RegenerateAction(this.shieldRegeneration, this.hullRegeneration);
	}

}
