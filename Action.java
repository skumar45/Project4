/**
 * An action that can be taken by an entity.
 * Actions can be either an activity (involving movement, gaining health, etc)
 * or an animation (updating the image being displayed).
 */
public final class Action {
    private final ActionKind kind;
    private final Entity entity;
    private final WorldModel world;
    private final ImageStore imageStore;
    private final int repeatCount;

    public Action(ActionKind kind, Entity entity, WorldModel world, ImageStore imageStore, int repeatCount) {
        this.kind = kind;
        this.entity = entity;
        this.world = world;
        this.imageStore = imageStore;
        this.repeatCount = repeatCount;
    }
    public void executeAction(EventScheduler scheduler) {
        switch (kind) {
            case ACTIVITY:
                this.executeActivityAction(scheduler);
                break;

            case ANIMATION:
                this.executeAnimationAction(scheduler);
                break;
        }
    }
    private void executeAnimationAction(EventScheduler scheduler) {
        entity.nextImage();

        if (repeatCount != 1) {
            scheduler.scheduleEvent(entity, Factory.createAnimationAction(entity, Math.max(repeatCount - 1, 0)), entity.getAnimationPeriod());
        }
    }

    private void executeActivityAction(EventScheduler scheduler) {
        switch (entity.getKind()) {
            case SAPLING:
                entity.executeSaplingActivity(world, imageStore, scheduler);
                break;
            case TREE:
                entity.executeTreeActivity(world, imageStore, scheduler);
                break;
            case FAIRY:
                entity.executeFairyActivity(world, imageStore, scheduler);
                break;
            case PERSON_SEARCHING:
                entity.executePersonSearchingActivity(world, imageStore, scheduler);
                break;
            case PERSON_FULL:
                entity.executePersonFullActivity(world, imageStore, scheduler);
                break;
            case CAT:
                entity.executeCatActivity(world, imageStore, scheduler);
                break;
            case PINK:
                entity.executePinkActivity(world, imageStore, scheduler);
                break;
            case ORANGE:
                entity.executeOrangeActivity(world, imageStore, scheduler);
                break;
            case DOG:
                entity.executeDogActivity(world, imageStore, scheduler);
                break;
            default:
                throw new UnsupportedOperationException(String.format("executeActivityAction not supported for %s", entity.getKind()));
        }
    }


}
