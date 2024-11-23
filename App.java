import java.io.File;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.AclEntry;
import java.nio.file.attribute.AclEntryFlag;
import java.nio.file.attribute.AclEntryPermission;
import java.nio.file.attribute.AclEntryType;
import java.nio.file.attribute.AclFileAttributeView;
import java.nio.file.attribute.UserPrincipal;
import java.util.List;
import java.util.Set;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        File file = new File("folder-1");

        if (file.exists()) {
            System.out.println("File exists\n");
        } else {
            System.out.println("File not exists");
            sc.close();
            return;
        }   

        Path filePath = Paths.get(file.getName());
        // printAcl(filePath);

        // String user = "car";
        System.out.print("User : ");
        String user = sc.next();
        try{
            setAcl(filePath, user);
            if(Files.isDirectory(filePath)){
                setAclRecursively(filePath, user);
            }
            System.out.println("==ACL set==");
        } catch(Exception e){
            System.out.println("Error: " + e.getMessage());
        }
        sc.close();
    }

    public static void printAcl(Path filePath){
        AclFileAttributeView view = Files.getFileAttributeView(filePath, AclFileAttributeView.class);

        try{
            List<AclEntry> acl = view.getAcl();

            if (acl.isEmpty()) {
                System.out.println("No ACL entries found for this file.");
            } else {
                System.out.println("ACL entries for the file '" + filePath + "':");
                int idx = 1;
                for (AclEntry entry : acl) {
                    System.out.println(idx + ". " +entry);
                    idx++;
                }
            }
        }
        catch(Exception e){
            e.getMessage();
        }
    }

    public static void setAcl(Path filePath, String user) throws Exception{
        UserPrincipal userPrincipal = filePath.getFileSystem().getUserPrincipalLookupService().lookupPrincipalByName(user);

        AclFileAttributeView aclView = Files.getFileAttributeView(filePath, AclFileAttributeView.class);

        AclEntry aclEntry = AclEntry.newBuilder()
            .setType(AclEntryType.ALLOW)
            .setPrincipal(userPrincipal)
            .setFlags(AclEntryFlag.DIRECTORY_INHERIT, AclEntryFlag.FILE_INHERIT)
            // .setPermissions(readPermissions)
            // .setPermissions(writePermissions)
            // .setPermissions(executePermissions)
            .setPermissions(rwxPermissions)
            .build();

        List<AclEntry> acl = aclView.getAcl();
        acl.add(0, aclEntry);
        aclView.setAcl(acl);
    }

    public static void setAclRecursively(Path filePath, String user) throws Exception{
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(filePath)) {
            for (Path entry: stream) {
                setAcl(entry,user);
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    static Set<AclEntryPermission> readPermissions = Set.of(
        AclEntryPermission.READ_DATA, 
        AclEntryPermission.READ_ACL, 
        AclEntryPermission.READ_ATTRIBUTES, 
        AclEntryPermission.READ_NAMED_ATTRS
    );

    static Set<AclEntryPermission> writePermissions = Set.of(
        AclEntryPermission.WRITE_DATA, 
        AclEntryPermission.APPEND_DATA, 
        AclEntryPermission.WRITE_ATTRIBUTES, 
        AclEntryPermission.WRITE_NAMED_ATTRS,
        AclEntryPermission.DELETE,
        AclEntryPermission.DELETE_CHILD
    );

    static Set<AclEntryPermission> executePermissions = Set.of(
        AclEntryPermission.EXECUTE
    );

    static Set<AclEntryPermission> rwxPermissions = Set.of(
        AclEntryPermission.READ_DATA, 
        AclEntryPermission.READ_ACL, 
        AclEntryPermission.READ_ATTRIBUTES, 
        AclEntryPermission.READ_NAMED_ATTRS,
        AclEntryPermission.WRITE_DATA, 
        AclEntryPermission.APPEND_DATA, 
        AclEntryPermission.WRITE_ATTRIBUTES, 
        AclEntryPermission.WRITE_NAMED_ATTRS,
        AclEntryPermission.DELETE,
        AclEntryPermission.DELETE_CHILD,
        AclEntryPermission.EXECUTE
    );
}
