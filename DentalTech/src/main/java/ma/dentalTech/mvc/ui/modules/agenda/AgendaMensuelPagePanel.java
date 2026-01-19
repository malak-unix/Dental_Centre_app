package ma.dentalTech.mvc.ui.modules.agenda;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.entities.agenda.PlageHoraire;
import ma.dentalTech.entities.enums.Mois;
import ma.dentalTech.mvc.controllers.modules.agenda.api.AgendaController;
import ma.dentalTech.mvc.dto.agenda.AgendaMensuelDto;
import ma.dentalTech.mvc.dto.agenda.DetailJourneeDto;
import ma.dentalTech.mvc.ui.common.CardPanel;
import ma.dentalTech.mvc.ui.common.DentalButton;
import ma.dentalTech.mvc.ui.common.DentalTheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class AgendaMensuelPagePanel extends JPanel {

    private final AgendaController controller;

    private final DefaultTableModel agendaModel;
    private final DefaultTableModel jourModel;

    private Long selectedAgendaId;
    private Long selectedDetailId;
    private Long medecinIdFilter = null;
    private boolean medecinLocked = false;

    public AgendaMensuelPagePanel() {
        setLayout(new BorderLayout(12, 12));
        setOpaque(false);

        controller = ApplicationContext.getBean(AgendaController.class);

        CardPanel top = new CardPanel("Agendas mensuels");
        add(top, BorderLayout.NORTH);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        actions.setOpaque(false);

        DentalButton refresh = new DentalButton("Rafraichir");
        DentalButton addAgenda = new DentalButton("Creer agenda");
        DentalButton addJour = new DentalButton("Ajouter jour");
        DentalButton markIndispo = new DentalButton("Indisponible");
        DentalButton plages = new DentalButton("Plages horaires");
        addJour.setEnabled(false);
        markIndispo.setEnabled(false);
        plages.setEnabled(false);

        actions.add(refresh);
        actions.add(addAgenda);
        actions.add(addJour);
        actions.add(markIndispo);
        actions.add(plages);
        top.add(actions, BorderLayout.CENTER);

        JPanel center = new JPanel(new GridLayout(1, 2, 12, 12));
        center.setOpaque(false);
        add(center, BorderLayout.CENTER);

        CardPanel agendasCard = new CardPanel("Liste des agendas");
        center.add(agendasCard);

        agendaModel = new DefaultTableModel(new Object[]{"ID", "Medecin", "Mois", "Annee"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable agendaTable = new JTable(agendaModel);
        agendaTable.setRowHeight(26);
        agendaTable.setFont(DentalTheme.textFont(12));
        agendaTable.getTableHeader().setFont(DentalTheme.textBold(12));
        agendasCard.add(new JScrollPane(agendaTable), BorderLayout.CENTER);

        CardPanel joursCard = new CardPanel("Details journees");
        center.add(joursCard);

        jourModel = new DefaultTableModel(new Object[]{"ID", "Date", "Debut", "Fin", "Etat", "Commentaire"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable jourTable = new JTable(jourModel);
        jourTable.setRowHeight(26);
        jourTable.setFont(DentalTheme.textFont(12));
        jourTable.getTableHeader().setFont(DentalTheme.textBold(12));
        joursCard.add(new JScrollPane(jourTable), BorderLayout.CENTER);

        refresh.addActionListener(e -> loadAgendas());
        addAgenda.addActionListener(e -> createAgendaDialog());
        addJour.addActionListener(e -> createJourneeDialog());
        markIndispo.addActionListener(e -> markIndisponible());
        plages.addActionListener(e -> managePlagesDialog());

        agendaTable.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int row = agendaTable.getSelectedRow();
            if (row >= 0) {
                Object idObj = agendaModel.getValueAt(row, 0);
                selectedAgendaId = idObj == null ? null : Long.valueOf(idObj.toString());
                loadJours(selectedAgendaId);
                addJour.setEnabled(selectedAgendaId != null);
                markIndispo.setEnabled(false);
                plages.setEnabled(false);
            }
        });

        jourTable.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int row = jourTable.getSelectedRow();
            if (row >= 0) {
                Object idObj = jourModel.getValueAt(row, 0);
                selectedDetailId = idObj == null ? null : Long.valueOf(idObj.toString());
                markIndispo.setEnabled(selectedDetailId != null);
                plages.setEnabled(selectedDetailId != null);
            }
        });

        loadAgendas();
    }

    public void setMedecinId(Long medecinId, boolean locked) {
        this.medecinIdFilter = medecinId;
        this.medecinLocked = locked;
    }

    public void reload() {
        loadAgendas();
    }

    private void loadAgendas() {
        try {
            if (controller == null) throw new IllegalStateException("AgendaController introuvable (ApplicationContext)");
            List<AgendaMensuelDto> list = controller.getAllAgendas();
            if (medecinIdFilter != null) {
                Long filter = medecinIdFilter;
                List<AgendaMensuelDto> filtered = new java.util.ArrayList<>();
                if (list != null) {
                    for (AgendaMensuelDto a : list) {
                        if (a != null && filter.equals(a.getMedecinId())) {
                            filtered.add(a);
                        }
                    }
                }
                list = filtered;
            }

            agendaModel.setRowCount(0);
            if (list != null) {
                for (AgendaMensuelDto a : list) {
                    agendaModel.addRow(new Object[]{a.getId(), a.getMedecinId(), a.getMois(), a.getAnnee()});
                }
            }

            jourModel.setRowCount(0);
            selectedAgendaId = null;
            selectedDetailId = null;

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur Agenda", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadJours(Long agendaId) {
        try {
            if (controller == null) throw new IllegalStateException("AgendaController introuvable");
            if (agendaId == null) {
                jourModel.setRowCount(0);
                return;
            }

            List<DetailJourneeDto> jours = controller.getDetailJourneesByAgendaId(agendaId);

            jourModel.setRowCount(0);
            if (jours != null) {
                for (DetailJourneeDto d : jours) {
                    jourModel.addRow(new Object[]{
                            d.getId(),
                            d.getDateJour(),
                            d.getHeureDebutTravail(),
                            d.getHeureFinTravail(),
                            d.getEtatJour(),
                            d.getCommentaire()
                    });
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur Detail journee", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void createAgendaDialog() {
        JDialog d = new JDialog(SwingUtilities.getWindowAncestor(this), "Creer agenda mensuel", Dialog.ModalityType.APPLICATION_MODAL);
        d.setLayout(new BorderLayout(10, 10));

        JComboBox<MedecinItem> cbMedecin = new JComboBox<>(loadMedecins(medecinIdFilter));
        if (medecinLocked) {
            cbMedecin.setEnabled(false);
        }
        JComboBox<Mois> cbMois = new JComboBox<>(Mois.values());
        JTextField tfAnnee = new JTextField(String.valueOf(LocalDate.now().getYear()));

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("Medecin"));
        form.add(cbMedecin);
        form.add(new JLabel("Mois"));
        form.add(cbMois);
        form.add(new JLabel("Annee"));
        form.add(tfAnnee);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancel = new JButton("Annuler");
        JButton save = new JButton("Creer");
        actions.add(cancel);
        actions.add(save);

        cancel.addActionListener(e -> d.dispose());
        save.addActionListener(e -> {
            MedecinItem m = (MedecinItem) cbMedecin.getSelectedItem();
            if (m == null) {
                JOptionPane.showMessageDialog(d, "Medecin obligatoire.");
                return;
            }
            AgendaMensuelDto dto = AgendaMensuelDto.builder()
                    .medecinId(m.id)
                    .mois((Mois) cbMois.getSelectedItem())
                    .annee(Integer.parseInt(tfAnnee.getText().trim()))
                    .build();
            controller.createAgenda(dto);
            d.dispose();
            loadAgendas();
        });

        d.add(form, BorderLayout.CENTER);
        d.add(actions, BorderLayout.SOUTH);
        d.pack();
        d.setSize(420, 220);
        d.setLocationRelativeTo(this);
        d.setVisible(true);
    }

    private void createJourneeDialog() {
        if (selectedAgendaId == null) {
            JOptionPane.showMessageDialog(this, "Selectionne un agenda d'abord.");
            return;
        }

        JDialog d = new JDialog(SwingUtilities.getWindowAncestor(this), "Ajouter journee", Dialog.ModalityType.APPLICATION_MODAL);
        d.setLayout(new BorderLayout(10, 10));

        JTextField tfDate = new JTextField(LocalDate.now().toString());
        JTextField tfDebut = new JTextField("09:00");
        JTextField tfFin = new JTextField("17:00");
        JComboBox<String> cbEtat = new JComboBox<>(new String[]{"OUVERT", "FERME", "FERIE", "VACANCES"});
        JTextField tfComment = new JTextField();

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("Date (yyyy-MM-dd)"));
        form.add(tfDate);
        form.add(new JLabel("Debut"));
        form.add(tfDebut);
        form.add(new JLabel("Fin"));
        form.add(tfFin);
        form.add(new JLabel("Etat"));
        form.add(cbEtat);
        form.add(new JLabel("Commentaire"));
        form.add(tfComment);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancel = new JButton("Annuler");
        JButton save = new JButton("Ajouter");
        actions.add(cancel);
        actions.add(save);

        cancel.addActionListener(e -> d.dispose());
        save.addActionListener(e -> {
            DetailJourneeDto dto = DetailJourneeDto.builder()
                    .agendaId(selectedAgendaId)
                    .dateJour(LocalDate.parse(tfDate.getText().trim()))
                    .heureDebutTravail(LocalTime.parse(tfDebut.getText().trim()))
                    .heureFinTravail(LocalTime.parse(tfFin.getText().trim()))
                    .etatJour(String.valueOf(cbEtat.getSelectedItem()))
                    .commentaire(tfComment.getText())
                    .build();
            controller.createDetailJournee(dto);
            d.dispose();
            loadJours(selectedAgendaId);
        });

        d.add(form, BorderLayout.CENTER);
        d.add(actions, BorderLayout.SOUTH);
        d.pack();
        d.setSize(460, 260);
        d.setLocationRelativeTo(this);
        d.setVisible(true);
    }

    private void markIndisponible() {
        if (selectedDetailId == null) {
            JOptionPane.showMessageDialog(this, "Selectionne une journee.");
            return;
        }

        DetailJourneeDto d = controller.getDetailJourneeById(selectedDetailId);
        if (d == null) return;
        d.setEtatJour("FERME");
        controller.updateDetailJournee(d);
        loadJours(selectedAgendaId);
    }

    private void managePlagesDialog() {
        if (selectedDetailId == null) {
            JOptionPane.showMessageDialog(this, "Selectionne une journee.");
            return;
        }

        JDialog d = new JDialog(SwingUtilities.getWindowAncestor(this), "Plages horaires", Dialog.ModalityType.APPLICATION_MODAL);
        d.setLayout(new BorderLayout(10, 10));

        DefaultTableModel plageModel = new DefaultTableModel(new Object[]{"ID", "Debut", "Fin", "Dispo"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable plageTable = new JTable(plageModel);
        plageTable.setRowHeight(24);

        JButton add = new JButton("Ajouter");
        JButton del = new JButton("Supprimer");

        Runnable reload = () -> {
            plageModel.setRowCount(0);
            List<PlageHoraire> list = controller.getPlagesByDetailJournee(selectedDetailId);
            if (list != null) {
                for (PlageHoraire p : list) {
                    plageModel.addRow(new Object[]{p.getId(), p.getHeureDebut(), p.getHeureFin(), p.getDisponible()});
                }
            }
        };
        reload.run();

        add.addActionListener(e -> {
            String h1 = JOptionPane.showInputDialog(d, "Heure debut (HH:mm)", "09:00");
            String h2 = JOptionPane.showInputDialog(d, "Heure fin (HH:mm)", "09:30");
            if (h1 == null || h2 == null) return;
            PlageHoraire p = new PlageHoraire();
            p.setDetailJourneeId(selectedDetailId);
            p.setHeureDebut(LocalTime.parse(h1.trim()));
            p.setHeureFin(LocalTime.parse(h2.trim()));
            p.setDisponible(true);
            controller.createPlage(p);
            reload.run();
        });

        del.addActionListener(e -> {
            int row = plageTable.getSelectedRow();
            if (row < 0) return;
            Long id = Long.valueOf(String.valueOf(plageModel.getValueAt(row, 0)));
            controller.deletePlage(id);
            reload.run();
        });

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.add(add);
        actions.add(del);

        d.add(new JScrollPane(plageTable), BorderLayout.CENTER);
        d.add(actions, BorderLayout.SOUTH);
        d.setSize(520, 320);
        d.setLocationRelativeTo(this);
        d.setVisible(true);
    }

    private static class MedecinItem {
        final Long id;
        final String label;
        MedecinItem(Long id, String label) { this.id = id; this.label = label; }
        @Override public String toString() { return label; }
    }

    private static DefaultComboBoxModel<MedecinItem> loadMedecins(Long onlyMedecinId) {
        DefaultComboBoxModel<MedecinItem> model = new DefaultComboBoxModel<>();
        try {
            var repo = new ma.dentalTech.repository.modules.users.impl.MedecinRepositoryImpl();
            List<ma.dentalTech.entities.users.Medecin> list;
            if (onlyMedecinId != null) {
                var m = repo.findById(onlyMedecinId);
                list = (m == null) ? List.of() : List.of(m);
            } else {
                list = repo.findAll();
            }
            if (list != null) {
                for (var m : list) {
                    String label = (m.getNom() + " " + m.getPrenom()).trim();
                    model.addElement(new MedecinItem(m.getId(), label));
                }
            }
        } catch (Exception ignored) {}
        return model;
    }
}
