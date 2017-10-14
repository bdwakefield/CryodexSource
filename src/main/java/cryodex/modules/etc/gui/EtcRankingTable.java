package cryodex.modules.etc.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import cryodex.CryodexController;
import cryodex.Player;
import cryodex.modules.RankingTable;
import cryodex.modules.Tournament;
import cryodex.modules.etc.EtcComparator;
import cryodex.modules.etc.EtcPlayer;
import cryodex.modules.etc.EtcTournament;
import cryodex.widget.ComponentUtils;
import cryodex.widget.TimerPanel;

public class EtcRankingTable extends RankingTable {

	private static final long serialVersionUID = 5587297504827909147L;

	private JTable table;
	private RankingTableModel model;
	private final EtcTournament tournament;
	private JLabel title;
	private JLabel statsLabel;
	
	private long lastResetTimestamp = 0;
	private long minResetWait = 1000;

	public EtcRankingTable(Tournament tournament) {
		super(new BorderLayout());
		JScrollPane scrollPane = new JScrollPane(getTable());
		ComponentUtils.forceSize(this, 400, 300);
		this.tournament = (EtcTournament) tournament;

		getTable().setFillsViewportHeight(true);

		updateLabel();
		JPanel labelPanel = ComponentUtils.addToVerticalBorderLayout(
				getTitleLabel(), getStatsLabel(), null);
		labelPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

		this.add(labelPanel, BorderLayout.NORTH);
		this.add(scrollPane, BorderLayout.CENTER);
		this.add(new TimerPanel(), BorderLayout.SOUTH);
	}

	private JLabel getStatsLabel() {
		if (statsLabel == null) {
			statsLabel = new JLabel();
		}
		return statsLabel;
	}

	private JLabel getTitleLabel() {
		if (title == null) {
			title = new JLabel("Player Rankings");
			title.setFont(new Font(title.getFont().getName(), title.getFont()
					.getStyle(), 20));
		}

		return title;
	}

	public void updateLabel() {
		int total = tournament.getAllPlayers().size();
		int active = tournament.getPlayers().size();

		if (total == 0) {
			total = active;
		}

		int dropped = total - active;
		if (total == active) {
			getStatsLabel().setText("Total Players: " + total);
		} else {
			getStatsLabel().setText(
					"Total Players: " + total + " Active Players: " + active
							+ " Dropped Players: " + dropped);
		}

	}

	private JTable getTable() {
		if (table == null) {
			table = new JTable(getTableModel());
			table.setDefaultRenderer(Object.class,
					new RankingTableCellRenderer());
			table.setDefaultRenderer(Integer.class,
					new RankingTableCellRenderer());
			table.getColumnModel().getColumn(0).setPreferredWidth(200);

			RankingTableCellRenderer centerRenderer = new RankingTableCellRenderer();
			centerRenderer.setHorizontalAlignment(JLabel.CENTER);

			table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
			table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
			table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
			table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
			table.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
		}
		return table;
	}

	private RankingTableModel getTableModel() {
		if (model == null) {
			model = new RankingTableModel(new ArrayList<Player>());
		}
		return model;
	}

	public void setPlayers(Set<Player> players) {

		List<Player> playerList = new ArrayList<Player>();
		playerList.addAll(players);

		Collections.sort(playerList, new EtcComparator(tournament,
				EtcComparator.rankingCompare));

		if (this.isVisible() == false) {
			this.setVisible(true);
		}
		getTableModel().setData(playerList);
		CryodexController.saveData();
		updateLabel();
	}

	public void resetPlayers() {
	    //This prevents multiple resets. Need a more permanent solution.
	    if(System.currentTimeMillis() - lastResetTimestamp > minResetWait){
    		getTableModel().resetData();
    		updateLabel();
    		lastResetTimestamp = System.currentTimeMillis();
	    }
	}
	
	public class RankingTableCellRenderer extends DefaultTableCellRenderer {

		private static final long serialVersionUID = 1L;

		@Override
	    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

	        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	        
	        // Working on adding color to the top 4/8/16 for easy visualization
//	        if(row < 4){
//	        	c.setBackground(Color.cyan);	
//	        } else if(row < 8){
//	        	c.setBackground(Color.green);
//	        } else if(row < 16){
//	        	c.setBackground(Color.yellow);
//	        } else if(row < 32){
//	        	c.setBackground(Color.red.brighter());
//	        }
	        
//	        if(row % 2 == 1){
//	        	c.setBackground(c.getBackground().darker());
//	        }
	        
//	        c.setForeground(Color.black);
	        
	        setBorder(noFocusBorder);
	        
	        return c;
	    }
	}

	private class RankingTableModel extends AbstractTableModel {

		private static final long serialVersionUID = -1591431777250055477L;

		private List<Player> data;

		public RankingTableModel(List<Player> data) {
			setData(data);
		}

		public void resetData() {
			Collections.sort(data, new EtcComparator(tournament,
					EtcComparator.rankingCompare));
			this.fireTableDataChanged();
		}

		public void setData(List<Player> data) {
			this.data = data;

			Collections.sort(data, new EtcComparator(tournament,
					EtcComparator.rankingCompare));
			this.fireTableDataChanged();
		}
		
		

		@Override
		public String getColumnName(int column) {
			String value = null;
			switch (column) {
			case 0:
				value = "Name";
				break;
			case 1:
				value = "Score";
				break;
			case 2:
				value = "MoV";
				break;
			case 3:
				value = "SoS";
				break;
			case 4:
				value = "Record";
				break;
			case 5:
				value = "Byes";
				break;
			}
			return value;
		}

		@Override
		public int getColumnCount() {
			return 6;
		}

		@Override
		public int getRowCount() {
			return data.size();
		}

		@Override
		public Object getValueAt(int arg0, int arg1) {
			EtcPlayer user = (EtcPlayer) data.get(arg0).getModuleInfoByModule(tournament.getModule());
			Object value = null;
			switch (arg1) {
			case 0:
				value = " " + user.getPlayer().getName();
				if (tournament.getPlayers().contains(user.getPlayer()) == false) {
					value = " (D#" + user.getRoundDropped(tournament) + ")"
							+ value;
				}
				value = "" + (arg0+1) + ". " + value;
				break;
			case 1:
				value = user.getScore(tournament);
				break;
			case 2:
				value = user.getMarginOfVictory(tournament);
				break;
			case 3:
				value = user.getAverageSoS(tournament);
				break;
			case 4:
				value = user.getWins(tournament) + " / "
						+ user.getLosses(tournament);
				break;
			case 5:
				value = user.getPlayer().getByes(tournament);
				break;

			}
			return value;
		}
	}

	public class NoCellSelectionRenderer extends DefaultTableCellRenderer {

		private static final long serialVersionUID = 1L;

		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			super.getTableCellRendererComponent(table, value, isSelected,
					hasFocus, row, column);
			setBorder(noFocusBorder);
			return this;
		}
	}

}