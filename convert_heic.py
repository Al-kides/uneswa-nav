import os
import sys
from PIL import Image #todo, add PNG, jpeg... 

#heic to webp converter. applciation of big J's suggestion (massive hack and win)
try:
    from pillow_heif import register_heif_opener
    register_heif_opener()
except ImportError:
    print("Error: pillow-heif not found. Run 'pip install pillow-heif' first.")
    sys.exit(1)

def convert_images(source_dir, target_dir):
    if not os.path.exists(target_dir):
        os.makedirs(target_dir)

    count = 0
    saved_size = 0

    for filename in os.listdir(source_dir):
        if filename.lower().endswith((".heic", ".jpg", ".png", ".jpeg", ".webp")):
            if filename == "logo.webp": continue 

            source_path = os.path.join(source_dir, filename)
            #snitize name for Android
            base_name = os.path.splitext(filename)[0]
            clean_name = "".join([c.lower() if c.isalnum() else "_" for c in base_name])
            while "__" in clean_name: clean_name = clean_name.replace("__", "_")
            clean_name = clean_name.strip("_")

            target_path = os.path.join(target_dir, f"{clean_name}.webp")

            try:
                img = Image.open(source_path)
                original_size = os.path.getsize(source_path)

                max_size = 1080
                if img.width > max_size or img.height > max_size:
                    img.thumbnail((max_size, max_size), Image.Resampling.LANCZOS)
                if img.mode in ("RGBA", "P"):
                    img = img.convert("RGB")
                img.save(target_path, "webp", quality=60, method=6)

                new_size = os.path.getsize(target_path)
                saved_size += (original_size - new_size)
                count += 1
                print(f"Optimized: {filename} -> {clean_name}.webp ({original_size//1024}KB -> {new_size//1024}KB)")
            except Exception as e:
                print(f"Error processing {filename}: {e}")

    print(f"\nFinished! Optimized {count} images.")
    print(f"Approximate space saved: {saved_size // (1024*1024)} MB")

if __name__ == "__main__":
    #just change the location to where you decide to move asssets... or if you generally have stuff in another folder you wanna convert
    #because ypu know... htis isn't just ... uneswa nav thing. it is an heic to webp..
    assets_dir = "./app/src/main/assets/drawable"
    convert_images(assets_dir, assets_dir)
