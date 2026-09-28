using System.Collections.Generic;
using UnityEngine;
using XienToiDe.Core;

namespace XienToiDe.Cooking
{
    public class CookingSlot
    {
        public int slotIndex;
        public CookingStation station;
        public FoodType foodType;
        public CookingState state = CookingState.Empty;
        public float timer = 0f;
        public float targetCookTime = 3f;
        public float burnLimitTime = 6f;
    }

    public class PlatedDish
    {
        public FoodType foodType;
        public SauceType sauce = SauceType.None;
    }

    public class CookingManager : MonoBehaviour
    {
        public static CookingManager Instance { get; private set; }

        public List<CookingSlot> slots = new List<CookingSlot>();
        public List<PlatedDish> platedDishes = new List<PlatedDish>();
        public int maxPlateCapacity = 3;

        private void Awake()
        {
            Instance = this;
            slots.Add(new CookingSlot { slotIndex = 0, station = CookingStation.BepChien });
            slots.Add(new CookingSlot { slotIndex = 1, station = CookingStation.BepChien });
            slots.Add(new CookingSlot { slotIndex = 2, station = CookingStation.BepNuongDa });
            slots.Add(new CookingSlot { slotIndex = 3, station = CookingStation.ThungDoUong });
        }

        private void Update()
        {
            foreach (var slot in slots)
            {
                if (slot.state == CookingState.Cooking)
                {
                    slot.timer += Time.deltaTime;
                    if (slot.timer >= slot.targetCookTime)
                    {
                        slot.state = CookingState.Done;
                    }
                }
                else if (slot.state == CookingState.Done && slot.station != CookingStation.ThungDoUong)
                {
                    slot.timer += Time.deltaTime;
                    if (slot.timer >= slot.burnLimitTime)
                    {
                        slot.state = CookingState.Burnt;
                    }
                }
            }
        }

        public bool PutFoodOnStove(FoodType food, CookingStation station, float cookTime, float burnTime)
        {
            var freeSlot = slots.Find(s => s.station == station && s.state == CookingState.Empty);
            if (freeSlot == null) return false;

            freeSlot.foodType = food;
            freeSlot.state = CookingState.Cooking;
            freeSlot.timer = 0f;
            freeSlot.targetCookTime = cookTime;
            freeSlot.burnLimitTime = burnTime;
            return true;
        }

        public bool PickupToPlate(int slotIndex)
        {
            if (platedDishes.Count >= maxPlateCapacity) return false;

            var slot = slots[slotIndex];
            if (slot.state == CookingState.Done)
            {
                platedDishes.Add(new PlatedDish { foodType = slot.foodType, sauce = SauceType.None });
                slot.state = CookingState.Empty;
                slot.timer = 0f;
                return true;
            }
            else if (slot.state == CookingState.Burnt)
            {
                slot.state = CookingState.Empty;
                slot.timer = 0f;
                return true;
            }
            return false;
        }

        public void ApplySauce(int plateIndex, SauceType sauce)
        {
            if (plateIndex >= 0 && plateIndex < platedDishes.Count)
            {
                platedDishes[plateIndex].sauce = sauce;
            }
        }

        public void RemovePlatedDish(int plateIndex)
        {
            if (plateIndex >= 0 && plateIndex < platedDishes.Count)
            {
                platedDishes.RemoveAt(plateIndex);
            }
        }
    }
}
