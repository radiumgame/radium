vec2 tile(sampler2D tex, vec2 tileSize) {
    tileSize = vec2(360) - (tileSize - 1);

    float verticalTiles = textureSize(tex, 0).x / tileSize.x;
    float horizontalTiles = textureSize(tex, 0).y / tileSize.y;
    vec2 tile = vec2(floor(verticalTiles * uv.x), floor(horizontalTiles * uv.y));
    vec2 tileUV = vec2(fract(verticalTiles * uv.x), fract(horizontalTiles * uv.y));
    float xResult;
    if (tile.x == 0.0) {
        xResult = tileUV.x / 3.0f;
    } else if (tile.x == verticalTiles - 1.0) {
        xResult = tileUV.x / 3.0 + (2.0 / 3.0);
    } else {
        xResult = tileUV.x / 3.0 + (1.0 / 3.0);
    }
    float yResult;
    if (tile.y == 0.0) {
        yResult = tileUV.y / 3.0f;
    } else if (tile.y == horizontalTiles - 1.0) {
        yResult = tileUV.y / 3.0 + (2.0 / 3.0);
    } else {
        yResult = tileUV.y / 3.0 + (1.0 / 3.0);
    }

    return vec2(xResult, yResult);
}