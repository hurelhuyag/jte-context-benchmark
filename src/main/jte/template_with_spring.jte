@import org.springframework.context.MessageSource
@import java.util.Locale
@param MessageSource ms
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
    <div>${ms.getMessage("message1", null, Locale.ENGLISH)}</div>
    <div>${ms.getMessage("message_1_param", new Object[]{p1}, null, Locale.ENGLISH)}</div>
    <div>${ms.getMessage("message_3_param", new Object[]{p1, p2, p3}, null, Locale.ENGLISH)}</div>
</body>
</html>
