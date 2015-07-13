
package jk2serverbrowser;

import javafx.application.Application;
import javafx.embed.swing.SwingNode;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javax.swing.SwingUtilities;
import jk2serverbrowser.fixtures.GameServerServiceFixtures;
import jk2serverbrowser.fixtures.MasterServerServiceFixtures;
import jk2serverbrowser.fixtures.RconFixtures;
import service.GameServerService;
import service.IRconService;
import service.MasterServerService;
import service.RconService;

/**
 *
 * @author Markus Mulkahainen
 */
public class Loader extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        final SwingNode swingNode = new SwingNode();              
        
        createSwingContent(swingNode, createController(getParameters().getRaw().toArray(new String[getParameters().getRaw().size()]), stage));
        
        BorderPane pane = new BorderPane();
        
        MenuBar menuBar = createMenuBar();
        pane.setTop(menuBar);
        pane.setCenter(swingNode);
        
        Scene scene = new Scene(pane, 1280, 720);
                
        //((StackPane) scene.getRoot()).getChildren().addAll(createMenuBar());

        stage.setTitle("JK2/JKA Server browser");
        stage.setScene(scene);
        stage.show();
    }
    
    private MenuBar createMenuBar() {
        Menu fileMenu = new Menu("File");
        Menu viewMenu = new Menu("View");
        Menu toolsMenu = new Menu("Tools");
        Menu helpMenu = new Menu("Help");
        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(fileMenu, viewMenu, toolsMenu, helpMenu);
        return menuBar;
    }
    
    private MainController createController(String[] args, Stage stage) {
        IMasterServerService masterService;
        IGameServerService gameService;
        IRconService rconService;
        
        if (args.length > 0 && args[0].equals("offline")) {
            masterService = new MasterServerServiceFixtures();
            gameService = new GameServerServiceFixtures();
            rconService = new RconFixtures();
        } else {
            masterService = new MasterServerService();
            gameService = new GameServerService();
            rconService = new RconService();
        }

        MainController controller = new MainController(masterService, gameService, rconService);
        controller.loadSettings();
        
        controller.serverSubject().subscribe(x -> {
            //stage.setTitle("JK2/JKA Server browser " + controller.getServers().size());
        });
        
        return controller;
    }
    
    private void createSwingContent(final SwingNode swingNode, MainController controller) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                swingNode.setContent(new Gui(controller));     
            }
        });
    }
    
}
