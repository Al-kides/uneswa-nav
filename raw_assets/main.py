#!/usr/bin/env python3
"""
Renames campus photos into drawable-compatible names and copies them
to app/src/main/res/drawable/.

Run from the project root:
    python3 rename_photos.py

Assumes your photos folder sits next to this script.
"""

import os
import shutil

# Maps folder name -> drawable prefix
FOLDERS = {
    "IDE(Institute of Distance Learning)": "ide",
    "Engineering Block":                   "engineering",
    "N_Class":                             "n_class",
    "Warden":                              "warden",
    "Commerce Block":                      "commerce",
}

SRC  = os.path.dirname(os.path.abspath(__file__))
DEST = os.path.join(SRC, "app", "src", "main", "res", "drawable")

def main():
    os.makedirs(DEST, exist_ok=True)
    copied = 0

    for folder, prefix in FOLDERS.items():
        src_dir = os.path.join(SRC, folder)

        if not os.path.isdir(src_dir):
            print(f"  SKIP  {folder}/ not found in photos/")
            continue

        # Sort so 1.heic < 2.heic < 10.heic etc.
        files = sorted(
            f for f in os.listdir(src_dir)
            if f.lower().endswith(".heic")
        )

        if not files:
            print(f"  SKIP  {folder}/ has no .heic files")
            continue

        for i, fname in enumerate(files, start=1):
            dst_name = f"{prefix}_{i}.heic"
            shutil.copy2(
                os.path.join(src_dir, fname),
                os.path.join(DEST, dst_name)
            )
            print(f"  OK    {folder}/{fname}  →  drawable/{dst_name}")
            copied += 1

    print(f"\n{copied} file(s) copied to {DEST}")

if __name__ == "__main__":
    main()
