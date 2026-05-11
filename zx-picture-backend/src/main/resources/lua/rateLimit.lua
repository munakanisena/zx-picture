local key = KEYS[1] -- 限流KEY
local limit = tonumber(ARGV[1]) -- 时间窗口内允许的请求数
local window = tonumber(ARGV[2]) -- 时间窗口大小(秒)

local current = tonumber(redis.call('GET', key) or "0") --获得当前计数

if current + 1 > limit then
    return 0
else
    current = redis.call("INCR", key)
    if current == 1 then
        redis.call("EXPIRE", key, window)
    end
    return 1
end