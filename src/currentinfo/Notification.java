package currentinfo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import iofiles.User;

public final class Notification {
    private String movieName;
    private String message;

    public Notification(final String movieName, final String message) {
        this.movieName = movieName;
        this.message = message;
    }

    /** method that adds a recommendation at the end of the actions */
    public static void addRecommendation(final ArrayNode outArrayNode) {
        if (Present.getInfo().getCurrentUser() != null) {
            if (Present.getInfo().getCurrentUser().getCredentials().getAccountType()
                    .equals("premium")) {
                Notification recommendation = new Notification("No recommendation",
                        "Recommendation");
                Present.getInfo().getCurrentUser().getNotifications().add(recommendation);
                ObjectMapper objectMapper = new ObjectMapper();
                ObjectNode lastCall = objectMapper.createObjectNode();
                lastCall.putPOJO("error", null);
                lastCall.putPOJO("currentMoviesList", null);
                User currentUser = new User(Present.getInfo().getCurrentUser());
                lastCall.putPOJO("currentUser", currentUser);
                outArrayNode.add(lastCall);
            }
        }
    }

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(final String movieName) {
        this.movieName = movieName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }
}
