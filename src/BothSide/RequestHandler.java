package BothSide;

public interface RequestHandler<Req, Res> {
     Res handle(Req req);
}
