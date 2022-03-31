@import io.github.hurelhuyag.jteutils.TemplateUtils
@param TemplateUtils mf
@param Object p1
@param Object p2
@param Object p3

<!DOCTYPE html>
<html lang="en">
<head>
    <title>Title</title>
    <meta charset="UTF-8">
</head>
<body>
    <div>${mf.formatMessage("message1")}</div>
    <div>${mf.formatMessage("message_1_param", p1)}</div>
    <div>${mf.formatMessage("message_3_param", p1, p2, p3)}</div>
</body>
</html>
