package dk.sgc.kubernetes.webhook;
import javax.ws.rs.*;
import io.fabric8.kubernetes.api.model.admission.v1.AdmissionReview;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/webhook")
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
public class WebhookService {

    @POST
    @Path("/mutate")
    public AdmissionReview  postMutate(AdmissionReview review) {
        System.out.println("Nu er vi her postMutate ");

        Jsonb jsonb = JsonbBuilder.create(new JsonbConfig().setProperty(JsonbConfig.FORMATTING, true));
        System.out.println("received admission review: {}" + jsonb.toJson(review));
        return review;
    }
}
