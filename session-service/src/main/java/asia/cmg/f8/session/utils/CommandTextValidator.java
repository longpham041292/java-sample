package asia.cmg.f8.session.utils;

public class CommandTextValidator {
	
	public static String getSafeKeyword(final String keyword){
        return keyword.replaceAll("(=)|(;)|(')|(DROP)|(DELETE)|(INSERT)|(EXEC)|(CREATE)", "");
	}
}
