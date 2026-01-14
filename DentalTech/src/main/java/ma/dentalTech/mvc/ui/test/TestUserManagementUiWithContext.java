package ma.dentalTech.mvc.ui.test;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.mvc.controllers.modules.users.api.UserManagementController;
import ma.dentalTech.mvc.ui.modules.users.UserManagementFrame;

import javax.swing.*;

public class TestUserManagementUiWithContext {

    public static void main(String[] args) {

        // ✅ "notification" simple : si ça passe ici, le context est initialisé
        System.out.println("✅ ApplicationContext initialisé");

        Object bean = ApplicationContext.getBean("userManagementController");
        if (!(bean instanceof UserManagementController ctrl)) {
            throw new IllegalStateException("❌ Bean userManagementController introuvable ou mauvais type : " + bean);
        }

        SwingUtilities.invokeLater(() -> new UserManagementFrame(ctrl).setVisible(true));
    }
}
