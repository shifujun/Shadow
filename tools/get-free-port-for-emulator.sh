#!/bin/bash
comm -23 <(seq 5554 2 5586 | sort) <(ss -tan | awk '{print $4}' | cut -d':' -f2 | grep "[0-9]\{1,5\}" | sort -u) | head -n 1