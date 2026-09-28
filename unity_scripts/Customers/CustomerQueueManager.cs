using System.Collections.Generic;
using UnityEngine;
using XienToiDe.Core;

namespace XienToiDe.Customers
{
    public class CustomerQueueManager : MonoBehaviour
    {
        public static CustomerQueueManager Instance { get; private set; }

        public List<CustomerOrder> activeOrders = new List<CustomerOrder>();
        public float spawnInterval = 4.5f;
        private float spawnTimer = 0f;

        private void Awake() => Instance = this;

        private void Update()
        {
            spawnTimer += Time.deltaTime;
            if (spawnTimer >= spawnInterval && activeOrders.Count < 5)
            {
                spawnTimer = 0f;
                SpawnRandomCustomer();
            }

            // Giảm độ kiên nhẫn
            for (int i = activeOrders.Count - 1; i >= 0; i--)
            {
                var order = activeOrders[i];
                order.currentPatience -= Time.deltaTime;
                if (order.currentPatience <= 0f)
                {
                    // Khách bỏ đi vì đợi lâu
                    activeOrders.RemoveAt(i);
                }
            }
        }

        public void SpawnRandomCustomer()
        {
            var customerTypes = new CustomerType[] {
                CustomerType.HocSinh,
                CustomerType.SinhVien,
                CustomerType.DuKhach,
                CustomerType.VangLai,
                CustomerType.Shipper
            };

            var type = customerTypes[Random.Range(0, customerTypes.Length)];
            var newOrder = new CustomerOrder
            {
                orderId = System.Guid.NewGuid().ToString(),
                customerType = type,
                isShipper = (type == CustomerType.Shipper),
                maxPatience = type == CustomerType.HocSinh ? 18f : (type == CustomerType.DuKhach ? 26f : 22f),
                currentPatience = type == CustomerType.HocSinh ? 18f : (type == CustomerType.DuKhach ? 26f : 22f),
                requiredSauce = (SauceType)Random.Range(1, 4)
            };

            newOrder.requestedItems.Add((FoodType)Random.Range(0, 4));
            if (type == CustomerType.SinhVien || type == CustomerType.DuKhach)
            {
                newOrder.requestedItems.Add((FoodType)Random.Range(0, 3));
            }

            activeOrders.Add(newOrder);
        }
    }
}
