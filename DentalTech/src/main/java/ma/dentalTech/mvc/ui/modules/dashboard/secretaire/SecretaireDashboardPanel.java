package ma.dentalTech.mvc.ui.modules.dashboard.secretaire;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.mvc.controllers.modules.agenda.api.ListeAttenteController;
import ma.dentalTech.mvc.controllers.modules.dashboard.api.DashboardController;
import ma.dentalTech.mvc.controllers.modules.patient.api.PatientController;
import ma.dentalTech.mvc.controllers.modules.users.api.NotificationController;
import ma.dentalTech.mvc.dto.agenda.ListeAttenteDto;
import ma.dentalTech.mvc.dto.agenda.RdvDto;
import ma.dentalTech.mvc.dto.dashboard.DashboardDTO;
import ma.dentalTech.mvc.dto.dashboard.common.NotificationDTO;
import ma.dentalTech.mvc.dto.dashboard.secretaire.SecretaireDashboardResponseDTO;
import ma.dentalTech.mvc.dto.patient.PatientFormDto;
import ma.dentalTech.mvc.dto.patient.PatientListDto;
import ma.dentalTech.mvc.ui.common.DentalButton;
import ma.dentalTech.mvc.ui.common.DentalTheme;
import ma.dentalTech.mvc.ui.modules.patient.PatientFormDialog;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SecretaireDashboardPanel extends JPanel {

    private final DashboardController controller;
    private final Long userId;
    private final Consumer<String> navigate;
    private final ListeAttenteController listeAttenteController;
    private final NotificationController notificationController;
    private final PatientController patientController;

    // Header
    private final JTextField searchField = new JTextField();

    // Revue du jour (cards)
    private final JLabel vPatients = valueLabel("0");
    private final JLabel vRecette  = valueLabel("0 DH");
    private final JLabel vRdv      = valueLabel("0");
    private final JLabel vAttente  = valueLabel("0");

        // File d'attente
    private final JPanel fileAttentePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 12));
    private final JLabel fileAttenteEmptyLabel = new JLabel("Aucun patient en attente aujourd'hui");

    // Notifications (après file d’attente)
    private final JPanel notificationsPanel = new JPanel();
    private  JScrollPane notificationsScroll;
    private final TitledBorder notifBorder = BorderFactory.createTitledBorder("NOTIFICATIONS");

        // Activites recentes
    private final JPanel activitiesLeft = new JPanel();

    // RDV du jour
    private final DefaultTableModel rdvModel = new DefaultTableModel(
            new Object[]{"Heure", "Patient", "Statut"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable rdvTable = new JTable(rdvModel);
    private final JPanel rdvCardPanel = new JPanel(new CardLayout());
    private final JLabel rdvEmptyLabel = new JLabel("Aucun rendez-vous aujourd'hui");
    private final List<RdvDto> rdvDuJourCache = new ArrayList<>();

    public SecretaireDashboardPanel(DashboardController controller, Long userId, Consumer<String> navigate) {
        this.controller = (controller != null)
                ? controller
                : ApplicationContext.getBean(DashboardController.class);
        this.userId = userId;
        this.navigate = (navigate != null) ? navigate : (k -> {});
        this.listeAttenteController = getBeanOrNull(ListeAttenteController.class, "listeAttente.controller");
        this.notificationController = getBeanOrNull(NotificationController.class, "notificationController");
        this.patientController = getBeanOrNull(PatientController.class, "patientController");

        setLayout(new BorderLayout(16, 16));
        setBackground(DentalTheme.BG);
        setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        // IMPORTANT : init notifications AVANT buildMain()
        notificationsPanel.setOpaque(false);
        notificationsPanel.setLayout(new BoxLayout(notificationsPanel, BoxLayout.Y_AXIS));

        notificationsScroll = new JScrollPane(notificationsPanel);
        notificationsScroll.setBorder(notifBorder);
        notificationsScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        notificationsScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        notificationsScroll.getVerticalScrollBar().setUnitIncrement(16);
        notificationsScroll.setPreferredSize(new Dimension(10, 240));
        notificationsScroll.setMinimumSize(new Dimension(10, 240));

        fileAttenteEmptyLabel.setForeground(DentalTheme.MUTED_TEXT);
        rdvEmptyLabel.setForeground(DentalTheme.MUTED_TEXT);
        rdvEmptyLabel.setHorizontalAlignment(SwingConstants.CENTER);

        rdvTable.setRowHeight(28);
        rdvTable.setFillsViewportHeight(true);
        rdvTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        rdvTable.getTableHeader().setReorderingAllowed(false);
        rdvTable.setFont(DentalTheme.textFont(12));
        rdvTable.getTableHeader().setFont(DentalTheme.textBold(12));
        rdvTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && rdvTable.getSelectedRow() >= 0) {
                    navigate.accept("rdv");
                }
            }
        });


        add(buildMain(), BorderLayout.CENTER);

        reload();
    }


    // ---------------- UI BUILD ----------------

    private JComponent buildHeader() {
        JPanel header = new JPanel(new BorderLayout(14, 0));
        header.setOpaque(false);

        // left brand (simple)
        JLabel brand = new JLabel("DENTAL CENTER");
        brand.setFont(DentalTheme.titleFont(18));
        brand.setForeground(DentalTheme.PRIMARY_DARK);

        // search bar
        JPanel searchBox = new JPanel(new BorderLayout(10, 0));
        searchBox.setOpaque(false);
        searchField.setPreferredSize(new Dimension(520, 36));
        searchField.setFont(DentalTheme.textFont(13));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DentalTheme.BORDER),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        searchField.setText("Rechercher un patient, ...");

        JButton searchBtn = new JButton("Rechercher");
        styleHeaderButton(searchBtn);
        searchBtn.addActionListener(e -> navigate.accept("patients"));

        searchBox.add(searchField, BorderLayout.CENTER);
        searchBox.add(searchBtn, BorderLayout.EAST);

        // right profile (simple)
        JPanel profile = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        profile.setOpaque(false);
        JLabel role = new JLabel("Secretaire");
        role.setFont(DentalTheme.textBold(13));
        role.setForeground(DentalTheme.PRIMARY_DARK);

        JLabel name = new JLabel("Mme Malak Achari");
        name.setFont(DentalTheme.textFont(12));
        name.setForeground(DentalTheme.MUTED_TEXT);

        JPanel t = new JPanel();
        t.setOpaque(false);
        t.setLayout(new BoxLayout(t, BoxLayout.Y_AXIS));
        t.add(role);
        t.add(name);

        JButton logout = new JButton("Deconnexion");
        styleHeaderButton(logout);
        logout.addActionListener(e -> navigate.accept("logout"));

        profile.add(t);
        profile.add(logout);

        header.add(brand, BorderLayout.WEST);
        header.add(searchBox, BorderLayout.CENTER);
        header.add(profile, BorderLayout.EAST);

        return header;
    }

    private JComponent buildMain() {
        JPanel root = new JPanel(new BorderLayout());
        root.setOpaque(false);

        JPanel columns = new JPanel(new GridBagLayout());
        columns.setOpaque(false);

        GridBagConstraints c = new GridBagConstraints();
        c.gridy = 0;
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 1;
        c.insets = new Insets(0, 0, 0, 0);

        c.gridx = 0;
        c.weightx = 0.68;
        columns.add(buildLeftColumn(), c);

        c.gridx = 1;
        c.weightx = 0.32;
        c.insets = new Insets(0, 14, 0, 0);
        columns.add(buildRightColumn(), c);

        root.add(columns, BorderLayout.CENTER);
        return root;
    }

    private JComponent buildLeftColumn() {
        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        left.add(buildSectionTitle("Revue du Jour"));
        left.add(Box.createVerticalStrut(8));
        left.add(buildRevueDuJour());
        left.add(Box.createVerticalStrut(12));
        left.add(buildRdvDuJourSection());
        left.add(Box.createVerticalStrut(12));
        left.add(buildFileAttenteSection());

        return left;
    }

    private JComponent buildRightColumn() {
        JPanel right = new JPanel();
        right.setOpaque(false);
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));

        right.add(notificationsScroll);
        right.add(Box.createVerticalStrut(12));
        right.add(buildActivitiesSection());

        return right;
    }

    private JComponent buildRevueDuJour() {
        JPanel container = cardContainer();
        container.setLayout(new BorderLayout(12, 12));

        JPanel cards = new JPanel(new GridLayout(1, 5, 12, 12));
        cards.setOpaque(false);

        cards.add(statCard("Nb patients", vPatients, ""));
        cards.add(statCard("Recette du jour", vRecette, ""));
        cards.add(statCard("RDV du jour", vRdv, ""));
        cards.add(statCard("Patients en attente", vAttente, ""));

        DentalButton stats = new DentalButton("Voir +statistiques");
        stats.addActionListener(e -> navigate.accept("caisse"));
        JPanel btnWrap = new JPanel(new GridBagLayout());
        btnWrap.setOpaque(false);
        btnWrap.add(stats);
        cards.add(btnWrap);

        container.add(cards, BorderLayout.CENTER);
        return container;
    }

    private JComponent buildRdvDuJourSection() {
        JPanel container = cardContainer();
        container.setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("RDV du jour");
        title.setFont(DentalTheme.titleFont(18));
        title.setForeground(DentalTheme.PRIMARY_DARK);

        rdvCardPanel.setOpaque(false);
        rdvCardPanel.removeAll();

        JScrollPane sp = new JScrollPane(rdvTable);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        sp.setPreferredSize(new Dimension(10, 160));
        sp.setMinimumSize(new Dimension(10, 160));

        JPanel emptyWrap = new JPanel(new GridBagLayout());
        emptyWrap.setOpaque(false);
        emptyWrap.add(rdvEmptyLabel);

        rdvCardPanel.add(sp, "TABLE");
        rdvCardPanel.add(emptyWrap, "EMPTY");

        container.add(title, BorderLayout.NORTH);
        container.add(rdvCardPanel, BorderLayout.CENTER);
        return container;
    }

    private JComponent buildFileAttenteSection() {
        JPanel container = cardContainer();
        container.setLayout(new BorderLayout(10, 10));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel title = new JLabel("FILE D'ATTENTE");
        title.setFont(DentalTheme.titleFont(18));
        title.setForeground(DentalTheme.PRIMARY_DARK);

        DentalButton addPatient = new DentalButton("+ Nouveau Patient");
        addPatient.addActionListener(e -> openAddFileAttenteDialog());

        top.add(title, BorderLayout.WEST);
        top.add(addPatient, BorderLayout.EAST);

        fileAttentePanel.setOpaque(false);
        JScrollPane sp = new JScrollPane(fileAttentePanel);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        sp.setPreferredSize(new Dimension(10, 220));
        sp.setMinimumSize(new Dimension(10, 220));

        container.add(top, BorderLayout.NORTH);
        container.add(sp, BorderLayout.CENTER);

        return container;
    }

    private void openAddFileAttenteDialog() {
        if (listeAttenteController == null) {
            JOptionPane.showMessageDialog(this, "Module file d'attente indisponible.", "Information",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Window owner = SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(owner, "Ajouter a la file d'attente", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(620, 380);
        dialog.setLocationRelativeTo(owner);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(DentalTheme.BG);

        JTabbedPane tabs = new JTabbedPane();
        tabs.add("Patient existant", buildExistingPatientTab(dialog));
        tabs.add("Nouveau patient", buildNewPatientTab(dialog));

        dialog.add(tabs, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private JPanel buildExistingPatientTab(JDialog dialog) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 8, 8, 8);
        c.fill = GridBagConstraints.HORIZONTAL;

        JTextField tfSearch = new JTextField();
        DefaultComboBoxModel<PatientListDto> model = new DefaultComboBoxModel<>();
        JComboBox<PatientListDto> combo = new JComboBox<>(model);
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                PatientListDto p = (PatientListDto) value;
                setText(p == null ? "" : patientLabel(p));
                return this;
            }
        });

        DentalButton btnSearch = new DentalButton("Rechercher");
        btnSearch.addActionListener(e -> loadPatients(model, tfSearch.getText()));

        loadPatients(model, "");

        JTextField tfMotif = new JTextField();
        JComboBox<String> cbPriorite = buildPrioriteCombo("NORMALE");

        c.gridx = 0; c.gridy = 0; c.weightx = 0;
        panel.add(label("Nom / Recherche"), c);
        c.gridx = 1; c.weightx = 1;
        panel.add(tfSearch, c);
        c.gridx = 2; c.weightx = 0;
        panel.add(btnSearch, c);

        c.gridx = 0; c.gridy = 1; c.weightx = 0;
        panel.add(label("Patient"), c);
        c.gridx = 1; c.gridwidth = 2; c.weightx = 1;
        panel.add(combo, c);
        c.gridwidth = 1;

        c.gridx = 0; c.gridy = 2; c.weightx = 0;
        panel.add(label("Motif"), c);
        c.gridx = 1; c.gridwidth = 2; c.weightx = 1;
        panel.add(tfMotif, c);
        c.gridwidth = 1;

        c.gridx = 0; c.gridy = 3; c.weightx = 0;
        panel.add(label("Priorite"), c);
        c.gridx = 1; c.gridwidth = 2; c.weightx = 1;
        panel.add(cbPriorite, c);
        c.gridwidth = 1;

        DentalButton add = new DentalButton("Ajouter a la file");
        add.addActionListener(e -> {
            PatientListDto selected = (PatientListDto) combo.getSelectedItem();
            if (selected == null || selected.getId() == null) {
                JOptionPane.showMessageDialog(panel, "Selectionnez un patient.", "Validation",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            addToFileAttente(selected.getId(), tfMotif.getText().trim(),
                    (String) cbPriorite.getSelectedItem());
            dialog.dispose();
        });

        c.gridx = 0; c.gridy = 4; c.gridwidth = 3;
        c.anchor = GridBagConstraints.EAST;
        panel.add(add, c);

        return panel;
    }

    private JPanel buildNewPatientTab(JDialog dialog) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 8, 8, 8);
        c.fill = GridBagConstraints.HORIZONTAL;

        JTextField tfMotif = new JTextField();
        JComboBox<String> cbPriorite = buildPrioriteCombo("NORMALE");

        c.gridx = 0; c.gridy = 0; c.weightx = 0;
        panel.add(label("Motif"), c);
        c.gridx = 1; c.weightx = 1;
        panel.add(tfMotif, c);

        c.gridx = 0; c.gridy = 1; c.weightx = 0;
        panel.add(label("Priorite"), c);
        c.gridx = 1; c.weightx = 1;
        panel.add(cbPriorite, c);

        DentalButton create = new DentalButton("Creer patient");
        create.addActionListener(e -> {
            if (patientController == null) {
                JOptionPane.showMessageDialog(panel, "Module patient indisponible.", "Information",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            PatientFormDialog form = new PatientFormDialog(dialog, "Nouveau patient", null);
            form.setVisible(true);
            if (!form.isConfirmed()) return;

            PatientFormDto created = patientController.creer(form.getDto());
            if (created == null || created.getId() == null) {
                JOptionPane.showMessageDialog(panel, "Creation patient echouee.", "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            int res = JOptionPane.showConfirmDialog(panel,
                    "Ajouter a la file d'attente maintenant ?", "Confirmation",
                    JOptionPane.YES_NO_OPTION);
            if (res == JOptionPane.YES_OPTION) {
                addToFileAttente(created.getId(), tfMotif.getText().trim(),
                        (String) cbPriorite.getSelectedItem());
            }
            dialog.dispose();
        });

        c.gridx = 0; c.gridy = 2; c.gridwidth = 2;
        c.anchor = GridBagConstraints.EAST;
        panel.add(create, c);

        return panel;
    }

    private void openEditFileAttenteDialog(ListeAttenteDto item) {
        if (listeAttenteController == null || item == null || item.getId() == null) return;

        Window owner = SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(owner, "Modifier file d'attente", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(480, 260);
        dialog.setLocationRelativeTo(owner);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(DentalTheme.BG);

        JPanel body = new JPanel(new GridBagLayout());
        body.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 8, 8, 8);
        c.fill = GridBagConstraints.HORIZONTAL;

        JTextField tfMotif = new JTextField(safe(item.getMotif()));
        JComboBox<String> cbPriorite = buildPrioriteCombo(firstNonBlank(item.getPriorite(), "NORMALE"));

        c.gridx = 0; c.gridy = 0; c.weightx = 0;
        body.add(label("Motif"), c);
        c.gridx = 1; c.weightx = 1;
        body.add(tfMotif, c);

        c.gridx = 0; c.gridy = 1; c.weightx = 0;
        body.add(label("Priorite"), c);
        c.gridx = 1; c.weightx = 1;
        body.add(cbPriorite, c);

        dialog.add(body, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setOpaque(false);
        DentalButton cancel = new DentalButton("Annuler");
        DentalButton save = new DentalButton("Enregistrer");

        cancel.addActionListener(e -> dialog.dispose());
        save.addActionListener(e -> {
            ListeAttenteDto updated = ListeAttenteDto.builder()
                    .id(item.getId())
                    .patientId(item.getPatientId())
                    .nom(item.getNom())
                    .motif(tfMotif.getText().trim())
                    .priorite((String) cbPriorite.getSelectedItem())
                    .dateAjout(item.getDateAjout())
                    .build();
            listeAttenteController.update(updated);
            JOptionPane.showMessageDialog(this, "File d'attente mise a jour.", "Confirmation",
                    JOptionPane.INFORMATION_MESSAGE);
            reload();
            dialog.dispose();
        });

        footer.add(cancel);
        footer.add(save);
        dialog.add(footer, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void addToFileAttente(Long patientId, String motif, String priorite) {
        if (listeAttenteController == null || patientId == null) return;

        ListeAttenteDto dto = ListeAttenteDto.builder()
                .patientId(patientId)
                .nom("File d'attente")
                .motif(motif == null || motif.isBlank() ? null : motif)
                .priorite(firstNonBlank(priorite, "NORMALE"))
                .dateAjout(LocalDateTime.now())
                .build();
        listeAttenteController.create(dto);
        JOptionPane.showMessageDialog(this, "Patient ajoute a la file d'attente.", "Confirmation",
                JOptionPane.INFORMATION_MESSAGE);
        reload();
    }

    private void loadPatients(DefaultComboBoxModel<PatientListDto> model, String query) {
        model.removeAllElements();
        if (patientController == null) return;
        try {
            List<PatientListDto> patients = (query == null || query.isBlank())
                    ? patientController.lister()
                    : patientController.rechercherParNom(query);
            for (PatientListDto p : patients) {
                model.addElement(p);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur chargement patients.", "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private JComboBox<String> buildPrioriteCombo(String selected) {
        JComboBox<String> cb = new JComboBox<>(new String[]{"BASSE", "NORMALE", "HAUTE"});
        cb.setSelectedItem(firstNonBlank(selected, "NORMALE"));
        return cb;
    }

    private JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(DentalTheme.textBold(12));
        l.setForeground(DentalTheme.TEXT2);
        return l;
    }

    private JComponent buildActivitiesSection() {
        JPanel wrap = cardContainer();
        wrap.setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("Activites recentes");
        title.setFont(DentalTheme.titleFont(18));
        title.setForeground(DentalTheme.PRIMARY_DARK);

        activitiesLeft.setOpaque(false);
        activitiesLeft.setLayout(new BoxLayout(activitiesLeft, BoxLayout.Y_AXIS));

        JScrollPane sp = new JScrollPane(activitiesLeft);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        sp.setPreferredSize(new Dimension(10, 240));
        sp.setMinimumSize(new Dimension(10, 240));

        DentalButton more = new DentalButton("Voir +Activites");
        more.addActionListener(e -> navigate.accept("rdv"));

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setOpaque(false);
        bottom.add(more);

        wrap.add(title, BorderLayout.NORTH);
        wrap.add(sp, BorderLayout.CENTER);
        wrap.add(bottom, BorderLayout.SOUTH);
        return wrap;
    }

    private JComponent buildSectionTitle(String text) {
        JLabel l = new JLabel(text);
        l.setFont(DentalTheme.titleFont(22));
        l.setForeground(DentalTheme.PRIMARY_DARK);

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.add(l, BorderLayout.WEST);
        return wrap;
    }

    // ---------------- DATA LOAD ----------------

    private void reload() {
        try {
            DashboardDTO dash = controller.getDashboardDTO(userId);
            SecretaireDashboardResponseDTO dto = dash.getSecretaire();
            if (dto == null) return;

            // Stats
            vPatients.setText(String.valueOf(nvl(dto.getNbPatients())));
            vRecette.setText(nvl(dto.getRecetteDuJour()) + " DH");
            vRdv.setText(String.valueOf(nvl(dto.getNbRdvDuJour())));
            vAttente.setText(String.valueOf(nvl(dto.getNbEnAttente())));

            // RDV du jour
            rdvDuJourCache.clear();
            rdvModel.setRowCount(0);
            List<RdvDto> rdvDuJour = dto.getRdvDuJour();
            if (rdvDuJour != null) {
                for (RdvDto r : rdvDuJour) {
                    rdvDuJourCache.add(r);
                    rdvModel.addRow(new Object[]{
                            formatHeure(r.getHeure()),
                            firstNonBlank(r.getPatientNom(), "Patient"),
                            r.getStatut() != null ? r.getStatut().name() : ""
                    });
                }
            }
            CardLayout rdvLayout = (CardLayout) rdvCardPanel.getLayout();
            rdvLayout.show(rdvCardPanel, rdvDuJourCache.isEmpty() ? "EMPTY" : "TABLE");
            rdvCardPanel.revalidate();
            rdvCardPanel.repaint();

        // File d'attente
            fileAttentePanel.removeAll();
            List<ListeAttenteDto> fileAttente = dto.getFileAttente();
            if (fileAttente != null && !fileAttente.isEmpty()) {
                for (ListeAttenteDto p : fileAttente) {
                    fileAttentePanel.add(buildPatientCard(p));
                }
            } else {
                fileAttentePanel.add(fileAttenteEmptyLabel);
            }
            fileAttentePanel.revalidate();
            fileAttentePanel.repaint();

            // Notifications (après file)
            notificationsPanel.removeAll();
            List<NotificationDTO> notif = dto.getNotifications();
            if (notif != null && !notif.isEmpty()) {
                for (NotificationDTO n : notif) {
                    notificationsPanel.add(buildNotificationCard(n));
                    notificationsPanel.add(Box.createVerticalStrut(8));
                }
            } else {
                JLabel empty = new JLabel("Aucune notification");
                empty.setForeground(DentalTheme.MUTED_TEXT);
                notificationsPanel.add(empty);
            }
            Integer unread = nvl(dto.getNbNotificationsNonLues());
            notifBorder.setTitle("NOTIFICATIONS" + (unread > 0 ? " (" + unread + " non lues)" : ""));
            notificationsScroll.repaint();
            notificationsPanel.revalidate();
            notificationsPanel.repaint();

        // Activites recentes
            activitiesLeft.removeAll();
            if (notif != null && !notif.isEmpty()) {
                for (int i = 0; i < Math.min(8, notif.size()); i++) {
                    activitiesLeft.add(buildActivityRow(notif.get(i), true));
                    activitiesLeft.add(Box.createVerticalStrut(8));
                }
            } else {
                JLabel e1 = new JLabel("Aucune activite recente");
                e1.setForeground(DentalTheme.MUTED_TEXT);
                activitiesLeft.add(e1);
            }
            activitiesLeft.revalidate();
            activitiesLeft.repaint();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // ---------------- COMPONENTS ----------------

    private JPanel cardContainer() {
        JPanel c = new JPanel();
        c.setBackground(DentalTheme.CARD);
        c.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DentalTheme.BORDER),
                BorderFactory.createEmptyBorder(14, 14, 14, 14)
        ));
        c.setOpaque(true);
        return c;
    }

    private JComponent statCard(String title, JLabel value, String icon) {
        JPanel card = new JPanel(new BorderLayout(10, 6));
        card.setOpaque(true);
        card.setBackground(DentalTheme.CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DentalTheme.BORDER),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));

        JLabel ic = null;
        if (icon != null && !icon.isBlank()) {
            ic = new JLabel(icon);
            ic.setFont(ic.getFont().deriveFont(22f));
        }

        JLabel t = new JLabel(title);
        t.setFont(DentalTheme.textFont(12));
        t.setForeground(DentalTheme.MUTED_TEXT);

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.add(t);
        center.add(Box.createVerticalStrut(6));
        center.add(value);

        if (ic != null) {
            card.add(ic, BorderLayout.WEST);
        }
        card.add(center, BorderLayout.CENTER);
        return card;
    }

    private JComponent buildPatientCard(ListeAttenteDto p) {
        JPanel card = new JPanel(new BorderLayout(10, 8));
        card.setPreferredSize(new Dimension(210, 130));
        card.setBackground(DentalTheme.CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DentalTheme.BORDER),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        String label = firstNonBlank(
                safe(p.getPatientNom()),
                safe(p.getNom()),
                "Patient"
        );

        JLabel name = new JLabel(label);
        name.setFont(DentalTheme.textBold(14));
        name.setForeground(DentalTheme.PRIMARY_DARK);

        JCheckBox passed = new JCheckBox();
        passed.setOpaque(false);
        passed.setToolTipText("Marquer comme passe");
        passed.setEnabled(listeAttenteController != null && p.getId() != null);
        passed.addActionListener(e -> {
            if (!passed.isSelected()) return;
            int ok = JOptionPane.showConfirmDialog(
                    this,
                    "Retirer ce patient de la file d'attente ?",
                    "Confirmation",
                    JOptionPane.YES_NO_OPTION
            );
            if (ok == JOptionPane.YES_OPTION && listeAttenteController != null && p.getId() != null) {
                listeAttenteController.deleteById(p.getId());
                reload();
            } else {
                passed.setSelected(false);
            }
        });

        JLabel motif = new JLabel("Motif: " + firstNonBlank(safe(p.getMotif()), "-"));
        motif.setFont(DentalTheme.textFont(11));
        motif.setForeground(DentalTheme.MUTED_TEXT);

        JLabel priorite = new JLabel("Priorite: " + firstNonBlank(safe(p.getPriorite()), "-"));
        priorite.setFont(DentalTheme.textFont(11));
        priorite.setForeground(DentalTheme.MUTED_TEXT);

        JPanel top = new JPanel(new BorderLayout(6, 0));
        top.setOpaque(false);
        top.add(passed, BorderLayout.WEST);
        top.add(name, BorderLayout.CENTER);

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.add(Box.createVerticalStrut(4));
        center.add(motif);
        center.add(priorite);

        DentalButton dossier = new DentalButton("Dossier");
        dossier.addActionListener(e -> navigate.accept("patients"));

        DentalButton modifier = new DentalButton("Modifier");
        modifier.setEnabled(listeAttenteController != null && p.getId() != null);
        modifier.addActionListener(e -> openEditFileAttenteDialog(p));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        actions.setOpaque(false);
        actions.add(dossier);
        actions.add(modifier);

        card.add(top, BorderLayout.NORTH);
        card.add(center, BorderLayout.CENTER);
        card.add(actions, BorderLayout.SOUTH);

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    navigate.accept("patients");
                }
            }
        });
        return card;
    }

    private JComponent buildNotificationCard(NotificationDTO n) {
        JPanel card = new JPanel(new BorderLayout(10, 6));
        card.setBackground(DentalTheme.CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DentalTheme.BORDER),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));

        JLabel ic = null;
        String icon = iconFor(n.getSource());
        if (icon != null && !icon.isBlank()) {
            ic = new JLabel(icon);
            ic.setFont(ic.getFont().deriveFont(18f));
            ic.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 8));
        }

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

        JLabel title = new JLabel(firstNonBlank(safe(n.getTitre()), "Notification"));
        title.setFont(DentalTheme.textBold(14));
        title.setForeground(DentalTheme.PRIMARY_DARK);

        JLabel msg = new JLabel("<html><div style='width:520px;'>" + escapeHtml(safe(n.getMessage())) + "</div></html>");
        msg.setForeground(DentalTheme.TEXT);

        center.add(title);
        center.add(Box.createVerticalStrut(4));
        center.add(msg);

        JPanel right = new JPanel();
        right.setOpaque(false);
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));

        JLabel time = new JLabel(timeAgo(n.getDate()));
        time.setForeground(DentalTheme.MUTED_TEXT);

        JLabel status = new JLabel(n.isLue() ? "Lue" : "Non lue");
        status.setForeground(n.isLue() ? DentalTheme.MUTED_TEXT : DentalTheme.PRIMARY_DARK);
        status.setFont(DentalTheme.textBold(12));

        right.add(time);
        right.add(Box.createVerticalStrut(6));
        right.add(status);

        if (!n.isLue() && notificationController != null && n.getId() != null) {
            DentalButton mark = new DentalButton("Marquer lue");
            mark.setFont(DentalTheme.textFont(11));
            mark.addActionListener(e -> {
                notificationController.markAsRead(n.getId());
                reload();
            });
            right.add(Box.createVerticalStrut(6));
            right.add(mark);
        }

        if (ic != null) {
            card.add(ic, BorderLayout.WEST);
        }
        card.add(center, BorderLayout.CENTER);
        card.add(right, BorderLayout.EAST);
        return card;
    }

    private JComponent buildActivityRow(NotificationDTO n, boolean big) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);

        JLabel dot = new JLabel(big ? "*" : "-");
        dot.setFont(dot.getFont().deriveFont(16f));

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

        String line = firstNonBlank(safe(n.getTitre()), safe(n.getMessage()), "Activite");
        JLabel txt = new JLabel("<html><div style='width:" + (big ? "520" : "360") + "px;'>" + escapeHtml(line) + "</div></html>");
        txt.setForeground(DentalTheme.TEXT);
        txt.setFont(DentalTheme.textFont(big ? 13 : 12));

        JLabel time = new JLabel(timeAgo(n.getDate()));
        time.setForeground(DentalTheme.MUTED_TEXT);
        time.setFont(DentalTheme.textFont(11));

        center.add(txt);
        center.add(Box.createVerticalStrut(2));
        center.add(time);

        row.add(dot, BorderLayout.WEST);
        row.add(center, BorderLayout.CENTER);
        return row;
    }

    private static JLabel valueLabel(String s) {
        JLabel l = new JLabel(s);
        l.setFont(DentalTheme.titleFont(18));
        l.setForeground(DentalTheme.PRIMARY_DARK);
        return l;
    }

    private void styleHeaderButton(AbstractButton b) {
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DentalTheme.BORDER),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        b.setBackground(DentalTheme.CARD);
    }

    private static <T> T getBeanOrNull(Class<T> type, String name) {
        try {
            T v = ApplicationContext.getBean(type);
            if (v != null) return v;
            if (name != null && !name.isBlank()) {
                T byName = ApplicationContext.getBeanByName(name, type);
                if (byName != null) return byName;
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    // ---------------- HELPERS ----------------

    private String iconFor(String src) {
        return "";
    }

    private String timeAgo(LocalDateTime dt) {
        if (dt == null) return "";
        Duration d = Duration.between(dt, LocalDateTime.now());
        long minutes = Math.max(0, d.toMinutes());
        if (minutes < 60) return "il y a " + minutes + " min";
        long hours = minutes / 60;
        if (hours < 24) return "il y a " + hours + " h";
        long days = hours / 24;
        return "il y a " + days + " j";
    }

    private String escapeHtml(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private String patientLabel(PatientListDto p) {
        if (p == null) return "";
        String nom = safe(p.getNomComplet());
        String tel = safe(p.getTelephone());
        if (!tel.isBlank()) return nom + " (" + tel + ")";
        return nom;
    }

    private String formatHeure(java.time.LocalTime t) {
        return t == null ? "" : t.toString();
    }

    private static String safe(String s) { return (s == null) ? "" : s; }

    private static String firstNonBlank(String... vals) {
        for (String v : vals) if (v != null && !v.isBlank()) return v;
        return "";
    }

    private static int nvl(Integer v) { return (v != null) ? v : 0; }
    private static String nvl(Object v) { return (v != null) ? String.valueOf(v) : "0"; }
}
