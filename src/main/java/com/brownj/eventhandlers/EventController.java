/**
 * Created by: James Brown
 * A simple gui control for copying file/files or Directory
 */

package com.brownj.eventhandlers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;


import java.io.IOException;
import java.nio.file.*;
import java.util.Iterator;


public class EventController {

    @FXML
    private TextField pathToCopyFrom;

    @FXML
    private TextField pathToCopyTo;

    @FXML
    private Button copyButton;

    @FXML
    private RadioButton copyFile;

    @FXML
    private RadioButton copyDirectory;

    @FXML
    private CheckBox clearTheFields;

    @FXML
    private Label completeLabel;

    @FXML
    public void initialize(){
        copyButton.setDisable(true);
        completeLabel.setVisible(false);
    }

    @FXML
    protected void onEventButtonClick() {
        /*
         * Action Event for copy button
         */
        try {
            // Copy file paths to strings
            String fileName = pathToCopyFrom.getText();
            String fileName1 = pathToCopyTo.getText();
            // Create Path object for each string
            Path copied = Paths.get(fileName1);
            Path originalPath = Paths.get(fileName);
            String s = Platform.isFxApplicationThread() ? "UI Thread" : "Background Thread";
            System.out.println("On thread: " + s);
            Runnable task = new Runnable() {
                @Override
                public void run() {
                    try {

                        if (copyFile.isSelected()) {
                            // Creates copy of File
                            Files.copy(originalPath, copied, StandardCopyOption.REPLACE_EXISTING);
                        } else {
                            /* If file is a Directory, create the directory and iterate over the files.
                             * Determine if any of those files are directories.
                             * If it is another Directory, create it and iterate over its contents and copy the files.
                             */
                            DirectoryStream<Path> contents = Files.newDirectoryStream(originalPath);
                            if(!Files.isDirectory(copied)){
                                Files.createDirectories(copied);
                            }

                            for (Path filePath : contents) {
                                String newPathStr = copied.toString() + "/" + filePath.getFileName();
                                Path newPath = Paths.get(newPathStr);
                                if (!Files.isDirectory(filePath)) {
                                    Files.copy(filePath, newPath, StandardCopyOption.REPLACE_EXISTING);
                                } else {
                                    Files.createDirectories(newPath);
                                    DirectoryStream<Path> tempContents = Files.newDirectoryStream(filePath);
                                    for(Path newFile : tempContents) {
                                        Path tempPath = Paths.get(newPath.toString() + "/" + newFile.getFileName());
                                        Files.copy(newFile, tempPath, StandardCopyOption.REPLACE_EXISTING);
                                    }
                                }
                            }

                        }
                        if(clearTheFields.isSelected()) {
                            pathToCopyFrom.clear();
                            pathToCopyTo.clear();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };

            if(completeLabel.isVisible()) {
                completeLabel.setVisible(false);
            }

            new Thread(task).start();
            completeLabel.setVisible(true);

        } catch (Exception e) {
            // IOException
            // NoSuchFileException
            e.printStackTrace();
        }


    }

    @FXML
    public void handleKeyRelease() {
        String fileName = pathToCopyFrom.getText();
        boolean disableButtons = fileName.isEmpty() || fileName.trim().isEmpty();
        copyButton.setDisable(disableButtons);
        completeLabel.setVisible(false);

    }

    // A way to identify which thread you are on.
    // String s = Platform.isFxApplicationThread() ? "UI Thread" : "Background Thread";
    // System.out.println("On thread: " + s);
}