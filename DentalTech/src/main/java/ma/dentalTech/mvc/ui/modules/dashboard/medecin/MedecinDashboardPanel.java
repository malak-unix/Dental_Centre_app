package ma.dentalTech.mvc.ui.modules.dashboard.medecin;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.mvc.controllers.modules.dashboard.api.DashboardController;
import ma.dentalTech.mvc.dto.agenda.RdvDto;
import ma.dentalTech.mvc.dto.dashboard.DashboardDTO;
import ma.dentalTech.mvc.dto.dashboard.medecin.MedecinDashboardResponseDTO;
import ma.dentalTech.mvc.ui.common.CardPanel;
import ma.dentalTech.mvc.ui.common.DentalButton;
import ma.dentalTech.mvc.ui.common.DentalTheme;
import ma.dentalTech.mvc.ui.common.UiStyles;
import ma.dentalTech.mvc.ui.common.components.StatCardPro;
import ma.dentalTech.mvc.ui.common.components.TeethChartPanel;
import ma.dentalTech.mvc.controllers.modules.dossierMedicale.api.DossierMedicalController;
import ma.dentalTech.mvc.ui.modules.dossierMedicale.dossier.DossierMedicalDetailUI;
import ma.dentalTech.repository.modules.dossierMedical.impl.DossierMedicalRepositoryImpl;
import ma.dentalTech.entities.dossierMedical.DossierMedical;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class MedecinDashboardPanel extends JPanel {

    private final DashboardController controller;
    private final Long userId;
    private final Consumer<String> navigate;
    private final DossierMedicalController dossierController;

    private final StatCardPro statPatients = new StatCardPro("Patients du jour", "0", "");
    private final StatCardPro statRdv      = new StatCardPro("RDV du jour", "0", "");
    private final StatCardPro statActes    = new StatCardPro("Actes realises", "0", "");
    private final StatCardPro statRecette  = new StatCardPro("Recette du jour", "0 DH", "");

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"Heure", "Patient", "Motif", "Statut"}, 0
    ) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };

    // Client en cours (infos)
    private final JLabel lblCurrentName = new JLabel("--");
    private final JLabel lblCurrentTel = new JLabel("");
    private final JLabel lblCurrentStatus = new JLabel("");
    private Long currentPatientId = null;
    private final List<RdvDto> rdvCache = new ArrayList<>();

    public MedecinDashboardPanel(DashboardController controller, Long userId, Consumer<String> navigate) {
        this.controller = (controller != null)
                ? controller
                : ApplicationContext.getBean(DashboardController.class);
        this.userId = userId;
        this.navigate = (navigate != null) ? navigate : (k -> {});
        DossierMedicalController dc = null;
        try { dc = ApplicationContext.getBean(DossierMedicalController.class); } catch (Exception ignored) {}
        this.dossierController = dc;

        setLayout(new BorderLayout(15, 15));
        setBackground(DentalTheme.BG);
        setOpaque(true);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        add(buildTopStats(), BorderLayout.NORTH);
        add(buildBody(), BorderLayout.CENTER);

        reload();
    }

    private JPanel buildTopStats() {
        JPanel p = new JPanel(new GridLayout(1, 4, 18, 18));
        p.setOpaque(false);
        p.add(statPatients);
        p.add(statRdv);
        p.add(statActes);
        p.add(statRecette);
        return p;
    }

    private JComponent buildBody() {
        JPanel root = new JPanel(new BorderLayout(15, 15));
        root.setOpaque(false);

        JTable table = new JTable(model);
        UiStyles.styleTable(table);
        table.setRowHeight(34);
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && table.getSelectedRow() >= 0) {
                    RdvDto r = getRdvAtRow(table.getSelectedRow());
                    if (r != null && r.getPatientId() != null) {
                        openDossierByPatientId(r.getPatientId());
                    }
                }
            }
        });
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createEmptyBorder());

        CardPanel rdvCard = new CardPanel("Rendez-vous du Jour");
        rdvCard.setLayout(new BorderLayout(10, 10));
        rdvCard.add(sp, BorderLayout.CENTER);

        root.add(rdvCard, BorderLayout.CENTER);

        CardPanel right = new CardPanel("Client en cours");
        right.setLayout(new BorderLayout(10, 10));
        right.setPreferredSize(new Dimension(360, 10));

        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));

        lblCurrentName.setFont(DentalTheme.textBold(16));
        lblCurrentName.setForeground(DentalTheme.TEXT1);
        lblCurrentTel.setFont(DentalTheme.textFont(12));
        lblCurrentTel.setForeground(DentalTheme.TEXT2);
        lblCurrentStatus.setFont(DentalTheme.textFont(12));
        lblCurrentStatus.setForeground(DentalTheme.TEXT2);
        lblCurrentName.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblCurrentName.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (currentPatientId() != null) {
                    openDossierByPatientId(currentPatientId());
                }
            }
        });

        info.add(lblCurrentName);
        info.add(Box.createVerticalStrut(2));
        info.add(lblCurrentTel);
        info.add(Box.createVerticalStrut(2));
        info.add(lblCurrentStatus);
        info.setBorder(BorderFactory.createEmptyBorder(6, 6, 0, 6));

        right.add(info, BorderLayout.NORTH);
        right.add(new TeethChartPanel(), BorderLayout.CENTER);

        JPanel btns = new JPanel(new GridLayout(4, 1, 8, 8));
        btns.setOpaque(false);

        DentalButton bDossier = new DentalButton("Dossier");
        UiStyles.stylePrimaryButton(bDossier);
        bDossier.addActionListener(e -> {
            if (currentPatientId() != null) openDossierByPatientId(currentPatientId());
        });

        DentalButton bConsult = new DentalButton("+ Consultation");
        UiStyles.stylePrimaryButton(bConsult);
        bConsult.addActionListener(e -> navigate.accept("consultations"));

        DentalButton bOrdo = new DentalButton("+ Ordonnance");
        UiStyles.stylePrimaryButton(bOrdo);
        bOrdo.addActionListener(e -> navigate.accept("ordonnances"));

        DentalButton bCertif = new DentalButton("+ Certificat");
        UiStyles.stylePrimaryButton(bCertif);
        bCertif.addActionListener(e -> navigate.accept("certificats"));

        btns.add(bDossier);
        btns.add(bConsult);
        btns.add(bOrdo);
        btns.add(bCertif);

        right.add(btns, BorderLayout.SOUTH);

        root.add(right, BorderLayout.EAST);

        return root;
    }

    private void reload() {
        try {
            DashboardDTO dash = controller.getDashboardDTO(userId);
            MedecinDashboardResponseDTO dto = dash.getMedecin();
            if (dto == null) return;

            statPatients.setValue(String.valueOf(nvl(dto.getNbPatientsDuJour())));
            statRdv.setValue(String.valueOf(nvl(dto.getNbRdvDuJour())));
            statActes.setValue(String.valueOf(nvl(dto.getNbActesRealises())));
            statRecette.setValue(nvl(dto.getRecetteDuJour()) + " DH");

            // client en cours
            if (dto.getPatientEnCours() != null) {
                currentPatientId = dto.getPatientEnCours().getPatientId();
                lblCurrentName.setText(dto.getPatientEnCours().getNomComplet() != null ? dto.getPatientEnCours().getNomComplet() : "--");
                lblCurrentTel.setText(dto.getPatientEnCours().getTel() != null ? ("Tel: " + dto.getPatientEnCours().getTel()) : "");
                lblCurrentStatus.setText(dto.getPatientEnCours().getStatutTraitement() != null ? ("Statut: " + dto.getPatientEnCours().getStatutTraitement()) : "");
            } else {
                currentPatientId = null;
                lblCurrentName.setText("--");
                lblCurrentTel.setText("");
                lblCurrentStatus.setText("");
            }

            rdvCache.clear();
            model.setRowCount(0);
            if (dto.getRdvDuJour() != null) {
                for (RdvDto r : dto.getRdvDuJour()) {
                    rdvCache.add(r);
                    model.addRow(new Object[]{
                            (r.getHeure() != null) ? r.getHeure().toString() : "-",
                            (r.getPatientNom() != null) ? r.getPatientNom() : "-",
                            (r.getMotif() != null) ? r.getMotif() : "-",
                            (r.getStatut() != null) ? r.getStatut().name() : "-"
                    });
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private RdvDto getRdvAtRow(int row) {
        if (row < 0 || row >= rdvCache.size()) return null;
        return rdvCache.get(row);
    }

    private Long currentPatientId() {
        return currentPatientId;
    }

    private void openDossierByPatientId(Long patientId) {
        if (patientId == null) return;
        if (dossierController == null) {
            JOptionPane.showMessageDialog(this, "Module dossier medical indisponible.", "Info",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        DossierMedicalRepositoryImpl repo = new DossierMedicalRepositoryImpl();
        Optional<DossierMedical> dossier = repo.findByPatientId(patientId);
        if (dossier.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Aucun dossier pour ce patient.", "Info",
                    JOptionPane.INFORMATION_MESSAGE);
            navigate.accept("dossiers");
            return;
        }

        Long dossierId = dossier.get().getId();
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Dossier medical",
                Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setLayout(new BorderLayout());
        dialog.add(new DossierMedicalDetailUI(dossierController, dossierId, dialog::dispose), BorderLayout.CENTER);
        dialog.pack();
        dialog.setSize(1200, 800);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private static int nvl(Integer v) { return (v != null) ? v : 0; }
    private static String nvl(Object v) { return (v != null) ? String.valueOf(v) : "0"; }
}
