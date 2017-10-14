package cryodex.modules.etc.wizard;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import cryodex.Language;
import cryodex.modules.Tournament.InitialSeedingEnum;
import cryodex.widget.ComponentUtils;
import cryodex.widget.SpringUtilities;
import cryodex.widget.wizard.TournamentWizard;
import cryodex.widget.wizard.WizardOptions;
import cryodex.widget.wizard.WizardUtils;
import cryodex.widget.wizard.pages.Page;

public class AdditionalOptionsPage implements Page {

	private JComboBox<String> playersPerTeamCombo;

	private JTextField customPointsTF;
	private JRadioButton standardRB;
	private JRadioButton escalationRB;
	private JRadioButton epicRB;
	private JRadioButton customRB;

	private JPanel pagePanel;

	@Override
	public JPanel getPanel() {

		TournamentWizard.getInstance().setButtonVisibility(true, null, true);

		TournamentWizard.getInstance().setMinimumSize(new Dimension(450, 500));

		if (pagePanel == null) {

			JPanel initialPairingPanel = new JPanel(new BorderLayout());

			JLabel header = new JLabel("<HTML><H3>" + Language.number_of_players_per_team + "</H3></HTML>");

			initialPairingPanel.add(ComponentUtils.addToFlowLayout(header, FlowLayout.LEFT), BorderLayout.NORTH);

			String[] optionsArray = { Language.three, Language.six };

			playersPerTeamCombo = new JComboBox<String>(optionsArray);

			initialPairingPanel.add(ComponentUtils.addToFlowLayout(playersPerTeamCombo, FlowLayout.LEFT),
					BorderLayout.CENTER);

			JPanel centerPanel = new JPanel(new BorderLayout());

			JLabel pointHeader = new JLabel("<HTML><H3>" + Language.choose_point_type + "</H3></HTML>");

			centerPanel.add(ComponentUtils.addToFlowLayout(pointHeader, FlowLayout.LEFT), BorderLayout.NORTH);

			JPanel pointsPanel = new JPanel(new SpringLayout());

			ButtonGroup pointsBG = new ButtonGroup();

			standardRB = new JRadioButton(Language.standard_points);
			escalationRB = new JRadioButton(Language.escalation_points);
			epicRB = new JRadioButton(Language.epic_points);
			customRB = new JRadioButton(Language.custom_points);

			final JLabel customPointsInfo = new JLabel(Language.comma_separated);

			ActionListener customListener = new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					if (customRB.isSelected()) {
						customPointsTF.setEnabled(true);
					} else {
						customPointsTF.setEnabled(false);
					}

				}
			};

			standardRB.addActionListener(customListener);
			escalationRB.addActionListener(customListener);
			epicRB.addActionListener(customListener);
			customRB.addActionListener(customListener);

			customPointsTF = new JTextField();
			customPointsTF.setColumns(12);
			customPointsTF.setEnabled(false);

			pointsBG.add(standardRB);
			pointsBG.add(escalationRB);
			pointsBG.add(epicRB);
			pointsBG.add(customRB);

			standardRB.setSelected(true);

			pointsPanel.add(standardRB);
			pointsPanel.add(escalationRB);
			pointsPanel.add(epicRB);
			pointsPanel.add(customRB);
			pointsPanel.add(customPointsInfo);

			pointsPanel.add(ComponentUtils.addToHorizontalBorderLayout(null,
					ComponentUtils.addToFlowLayout(customPointsTF, FlowLayout.LEFT), new JPanel()));

			SpringUtilities.makeCompactGrid(pointsPanel, 6, 1, 0, 0, 0, 0);

			centerPanel.add(ComponentUtils.addToFlowLayout(pointsPanel, FlowLayout.LEFT), BorderLayout.CENTER);

			pagePanel = new JPanel(new FlowLayout());

			pagePanel.add(ComponentUtils.addToVerticalBorderLayout(initialPairingPanel, centerPanel, null));
		}

		return pagePanel;
	}

	@Override
	public void onNext() {
		// Do nothing
	}

	@Override
	public void onPrevious() {
		TournamentWizard.getInstance().goToPrevious();
	}

	@Override
	public void onFinish() {

		WizardOptions wizardOptions = TournamentWizard.getInstance().getWizardOptions();

		if (standardRB.isSelected()) {
			wizardOptions.setPoints(100);
		} else if (escalationRB.isSelected()) {
			List<Integer> points = new ArrayList<Integer>();
			points.add(60);
			points.add(90);
			points.add(120);
			points.add(150);
			wizardOptions.setPoints(points);
		} else if (epicRB.isSelected()) {
			wizardOptions.setPoints(300);
		} else if (customRB.isSelected()) {
			try {
				Integer points = Integer.parseInt(customPointsTF.getText());
				wizardOptions.setPoints(points);
			} catch (Exception e) {
				String[] rounds = customPointsTF.getText().split(",");
				List<Integer> points = new ArrayList<Integer>();
				for (String s : rounds) {
					points.add(Integer.parseInt(s.trim()));
				}
				wizardOptions.setPoints(points);
			}

		}

		wizardOptions.setInitialSeedingEnum(InitialSeedingEnum.RANDOM);

		wizardOptions.setOption(Language.number_of_players_per_team, (String)playersPerTeamCombo.getSelectedItem());

		WizardUtils.createTournament();
	}
}