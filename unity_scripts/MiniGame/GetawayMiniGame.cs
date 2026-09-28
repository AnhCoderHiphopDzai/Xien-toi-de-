using UnityEngine;
using System;

namespace XienToiDe.MiniGame
{
    public class GetawayMiniGame : MonoBehaviour
    {
        public int playerLane = 1; // 0: Trái, 1: Giữa, 2: Phải
        public int cartDurability = 3; // 3 máu (hoặc 4 nếu có nâng cấp xe đẩy)
        public float distanceRemaining = 150f; // 150m tới ngõ an toàn
        public bool isGameOver = false;

        public float scrollSpeed = 12f;
        public int bonusMoneyCollected = 0;
        public int collisionDamageFine = 0;

        public event Action<bool, int, int> OnMiniGameCompleted; // (Thành công, Tiền thiệt hại, Tiền nhặt thêm)

        private void Update()
        {
            if (isGameOver) return;

            distanceRemaining -= scrollSpeed * Time.deltaTime;

            // Điều khiển chuyển làn (Phím Mũi Tên hoặc A/D trên PC, hoặc Vuốt trên Mobile)
            if (Input.GetKeyDown(KeyCode.LeftArrow) || Input.GetKeyDown(KeyCode.A))
            {
                if (playerLane > 0) playerLane--;
            }
            else if (Input.GetKeyDown(KeyCode.RightArrow) || Input.GetKeyDown(KeyCode.D))
            {
                if (playerLane < 2) playerLane++;
            }

            // Kiểm tra hoàn thành chặng đường
            if (distanceRemaining <= 0f)
            {
                isGameOver = true;
                OnMiniGameCompleted?.Invoke(true, collisionDamageFine, bonusMoneyCollected);
            }
        }

        public void OnHitObstacle(string obstacleName)
        {
            cartDurability--;
            collisionDamageFine += 20000; // Va quẹt xe Lead hoặc ổ gà hỏng xe

            if (cartDurability <= 0)
            {
                isGameOver = true;
                OnMiniGameCompleted?.Invoke(false, collisionDamageFine, bonusMoneyCollected);
            }
        }

        public void OnCollectBonus(int amount)
        {
            bonusMoneyCollected += amount;
        }
    }
}
