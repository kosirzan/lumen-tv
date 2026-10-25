#!/bin/sh
# Optional regeneration of the bundled, wholly synthetic MIT-licensed test clip.
# Requires FFmpeg with libx264 and AAC encoders. No downloaded movie assets.
set -eu
project_dir=$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)
ffmpeg -hide_banner -loglevel error -y \
  -f lavfi -i 'testsrc2=size=640x360:rate=24' \
  -f lavfi -i 'sine=frequency=440:sample_rate=44100' \
  -t 20 -c:v libx264 -preset veryfast -crf 30 -pix_fmt yuv420p \
  -profile:v baseline -level 3.0 -c:a aac -b:a 64k -movflags +faststart \
  "$project_dir/app/src/main/res/raw/diagnostic.mp4"
