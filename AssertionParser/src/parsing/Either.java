package parsing;

public class Either<LEFT, RIGHT> {
    private LEFT left;
    private RIGHT right;

    public static class EitherLeftCreator <LEFT>{
        private LEFT data;

        public EitherLeftCreator(LEFT l) {
            data = l;
        }

        public <RIGHT> Either<LEFT, RIGHT> get(){
            Either<LEFT, RIGHT> e = new Either<LEFT, RIGHT>();
            e.left = data;
            e.right = null;
            return e;
        }
    }

    public static <LEFT> EitherLeftCreator<LEFT> Left(LEFT l) {
        return new EitherLeftCreator<LEFT>(l);
    }

    public static class EitherRightCreator <RIGHT>{
        private RIGHT data;

        public EitherRightCreator(RIGHT l) {
            data = l;
        }

        public <LEFT> Either<LEFT, RIGHT> get(){
            Either<LEFT, RIGHT> e = new Either<LEFT, RIGHT>();
            e.left = null;
            e.right = data;
            return e;
        }
    }

    public static <RIGHT> EitherRightCreator<RIGHT> Right(RIGHT l) {
        return new EitherRightCreator<RIGHT>(l);
    }

    public boolean isLeft() {
        return left != null;
    }

    public LEFT left() {
        return left;
    }

    public RIGHT right() {
        return right;
    }
}
