import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.AclEntry;
import java.nio.file.attribute.AclEntryType;
import java.nio.file.attribute.AclEntryPermission;
import java.nio.file.attribute.AclFileAttributeView;
import java.nio.file.attribute.UserPrincipal;
import java.util.List;

public class App {
    public static void main(String[] args) {
        File file = new File("test.txt");

        if(file.exists()){
            System.out.println("ada");
        }
        else{
            System.out.println("tak ada");
        }

        // Specify the relative or absolute path to the file
        Path filePath = Paths.get("test.txt");  // If it's in the same directory, use a relative path



        try {
            // // Get the AclFileAttributeView of the file
            // AclFileAttributeView aclAttr = Files.getFileAttributeView(filePath, AclFileAttributeView.class);
            
            // if (aclAttr == null) {
            //     System.out.println("ACL view is not supported on this file system.");
            //     return;
            // }

            // // Get the ACL entries of the file
            // List<AclEntry> aclEntries = aclAttr.getAcl();

            // Check if there are any ACL entries and print them
            // if (aclEntries.isEmpty()) {
            //     System.out.println("No ACL entries found for this file.");
            // } else {
            //     System.out.println("ACL entries for the file '" + filePath + "':");
            //     for (AclEntry entry : aclEntries) {
            //         System.out.println(entry);
            //     }
            // }

            /////////////////////////////////
            // lookup "joe"
            UserPrincipal car = filePath.getFileSystem().getUserPrincipalLookupService()
            .lookupPrincipalByName("car");

            // get view
            AclFileAttributeView view = Files.getFileAttributeView(filePath, AclFileAttributeView.class);

            // create ACE to give "joe" read access
            AclEntry entry = AclEntry.newBuilder()
                .setType(AclEntryType.ALLOW)
                .setPrincipal(car)
                .setPermissions(AclEntryPermission.READ_DATA, AclEntryPermission.READ_NAMED_ATTRS, AclEntryPermission.READ_ATTRIBUTES, AclEntryPermission.READ_ACL)
                .build();

            // read ACL, insert ACE, re-write ACL
            List<AclEntry> acl = view.getAcl();
            acl.add(0, entry);   // insert before any DENY entries
            view.setAcl(acl);

            if (acl.isEmpty()) {
                System.out.println("No ACL entries found for this file.");
            } else {
                System.out.println("ACL entries for the file '" + filePath + "':");
                for (AclEntry entryhehe : acl) {
                    System.out.println(entryhehe);
                }
            }
            

        } catch (IOException e) {
            System.err.println("Error reading ACL entries: " + e.getMessage());
        }
    }
}
