package pro3.service_tier.events;

public class TrayFullEvent
{
  private final int trayId;

  public TrayFullEvent(int trayId)
  {
    this.trayId = trayId;
  }

  public int getTrayId()
  {
    return trayId;
  }
}