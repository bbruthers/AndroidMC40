package net.vantagesystems.VantageLicenseScanner;

public class ServerResponse
{
    private ServerTaskResultStatus ServerResponseStatus;
    private String ResponseData;

    public ServerResponse(){}

    public ServerResponse(String data, ServerTaskResultStatus status)
    {
        ResponseData = data;
        ServerResponseStatus = status;
    }

    public void SetResponseStatus(ServerTaskResultStatus strs)
    {
        ServerResponseStatus = strs;
    }

    public void SetData(String param)
    {
        ResponseData = param;
    }

    public ServerTaskResultStatus getServerResponseStatus()
    {
        return ServerResponseStatus;
    }

    public String getResponseData()
    {
        return ResponseData;
    }
}
