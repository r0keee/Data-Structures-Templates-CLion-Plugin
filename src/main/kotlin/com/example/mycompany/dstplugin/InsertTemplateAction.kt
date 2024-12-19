package com.example.mycompany.dstplugin

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages

class InsertTemplateAction : AnAction() {

    override fun actionPerformed(event: AnActionEvent) {
        val project: Project? = event.project

        val editor: Editor? = event.getData(com.intellij.openapi.actionSystem.CommonDataKeys.EDITOR)

        if (editor == null || project == null) { // if no editor or project, then showing an error
            Messages.showMessageDialog(
                project,
                "No active editor or project found!",
                "Error",
                Messages.getErrorIcon()
            )
            return
        }

        val document: Document = editor.document
        val caretModel = editor.caretModel
        val offset = caretModel.offset

        val options = arrayOf("Stack", "Queue", "BinaryTree", "BalancedBinaryTree", "Heap") // options
        val choice = Messages.showChooseDialog( // making dialogue choose window
            project,
            "Select a structure to insert:",
            "Insert Template",
            null,
            options, // options container
            options[0] // first option
        )

        if (choice == null) { // if no choice then exit
            return
        }

        val template = when (choice) { // iterating through choices and getting needed structure
            0 -> getStackTemplate()
            1 -> getQueueTemplate()
            2 -> getBinaryTreeTemplate()
            3 -> getBalancedBinaryTreeTemplate()
            4 -> getHeapTemplate()
            else -> null
        }

        if (template != null) { // if no structure then do nothing
            WriteCommandAction.runWriteCommandAction(project) {
                document.insertString(offset, "\n$template") // inserting structure in code
            }
        }
    }

    private fun getStackTemplate(): String {
        return """
            template <typename T>
            struct Node {
                T data;
                Node* next;
            };

            template <typename T>
            struct Stack {
                Node<T>* head = nullptr;

                void push(T data) {
                    Node<T>* new_node = new Node<T>();
                    new_node->data = data;
                    new_node->next = head;
                    head = new_node;
                }

                Node<T>* pop() {
                    if (head == nullptr) {
                        return nullptr;
                    }
                    Node<T>* return_data = head->data;
                    Node<T>* temp = head;
                    head = head->next;
                    delete temp;
                    return return_data;
                }
            };
        """.trimIndent()
    }

    private fun getQueueTemplate(): String {
        return """
            template <typename T>
            struct Node {
                T data;
                Node* next;
            };

            template <typename T>
            struct Queue {
                Node<T>* head = nullptr;
                Node<T>* tail = nullptr;
            
                void push(T data) {
                    Node<T>* new_node = new Node<T>();
                    new_node->data = data;
                    new_node->next = nullptr;
            
                    if (tail == nullptr) {
                        head = new_node;
                        tail = new_node;
                    } else {
                        tail->next = new_node;
                        tail = tail->next;
                    }
                }
            
                Node<T>* pop() {
                    if (head == nullptr) {
                        return nullptr;
                    }
                                
                    Node<T>* return_data = head->data;
                    Node<T>* temp = head;
                    head = head->next;
                    delete temp;
            
                    if (head == nullptr)
                        tail = nullptr;
            
                    return return_data;
                }
            };
        """.trimIndent()
    }

    private fun getBinaryTreeTemplate(): String {
        return """
            template <typename T>
            struct Node {
                T data;
                Node* left = nullptr;
                Node* right = nullptr;
            };

            template <typename T>
            Node* Insert(Node* root, T data) {
                if (root == nullptr) {
                    Node<T>* node = new Node<T>();
                    node->data = data;
                    return node;
                }
                if (data < root->data) {
                    root->left = Insert(root->left, data);
                } else {
                    root->right = Insert(root->right, data);
                }
                return root;
            }

            template <typename T>
            Node<T>* Minimum(Node<T>* root) {
                while (root->left != nullptr) {
                    root = root->left;
                }
                return root;
            }
            
            template <typename T>
            Node<T>* Delete(Node<T>* root, T data) {
                if (root == nullptr) {
                    return root;
                }

                Node<T>* temp = root;
                if (data < root->data) {
                    root->left = Delete(root->left, data);
                } else if (data > root->data) {
                    root->right = Delete(root->right, data);
                } else {
                    if (root->left != nullptr && root->right != nullptr) {
                        root->data = Minimum(root->right)->data;
                        root->right = Delete(root->right, root->data);
                    } else {
                        if (root->left != nullptr) {
                            temp = root->left;
                            delete root;
                        }
                        else if (root->right != nullptr) {
                            temp = root->right;
                            delete root;
                        }
                        else {
                            temp = nullptr;
                            delete root;
                        }
                    }
                }
                return temp;
            }
        """.trimIndent()
    }

    private fun getBalancedBinaryTreeTemplate(): String {
        return """
            template <typename T>
            struct Node {
                T data;
                long long int height = 1;
                Node* left = nullptr;
                Node* right = nullptr;
            };

            long long int max(long long int a, long long int b) {
                return a > b ? a : b;
            }

            template <typename T>
            long long int GetHeight(Node<T>* root) {
                if (root != nullptr) {
                    return root->height;
                } else {
                    return 0;
                }
            }

            template <typename T>
            void UpdateHeight(Node<T>* root) {
                if (root != nullptr) {
                    root->height = max(GetHeight(root->left), GetHeight(root->right)) + 1;
                }
            }
            
            template <typename T>
            Node<T>* SmallLeftRotate(Node<T>* root) {
                Node<T>* right_child = root->right;
                root->right = right_child->left;
                right_child->left = root;
                UpdateHeight(root);
                UpdateHeight(right_child);
                return right_child;
            }

            template <typename T>
            Node<T>* SmallRightRotate(Node<T>* root) {
                Node<T>* left_child = root->left;
                root->left = left_child->right;
                left_child->right = root;
                UpdateHeight(root);
                UpdateHeight(left_child);
                return left_child;
            }

            template <typename T>
            long long int GetBalance(Node<T>* root) {
                if (root != nullptr) {
                    return GetHeight(root->right) - GetHeight(root->left);
                } else {
                    return 0;
                }
            }

            template <typename T>
            Node<T>* Balance(Node<T>* root) {
                UpdateHeight(root);
                long long int balance = GetBalance(root);
                if (balance == 2) {
                    if (GetBalance(root->right) < 0) {
                        root->right = SmallRightRotate(root->right);
                    }
                    return SmallLeftRotate(root);
                }
                if (balance == -2) {
                    if (GetBalance(root->left) > 0) {
                        root->left = SmallLeftRotate(root->left);
                    }
                    return SmallRightRotate(root);
                }
                return root;
            }

            template <typename T>
            Node<T>* Insert(Node<T>* root, T data) {
                if (root == nullptr) {
                    Node<T>* new_node = new Node<T>();
                    new_node->data = data;
                    return new_node;
                }

                if (data < root->data) {
                    root->left = Insert(root->left, data);
                } else if (data > root->data) {
                    root->right = Insert(root->right, data);
                } else {
                    return root;
                }

                return Balance(root);
            }

            template <typename T>
            Node<T>* Maximum(Node<T>* root) {
                while(root->right != nullptr) {
                    root = root->right;
                }
                return root;
            }

            template <typename T>
            Node<T>* DeleteMax(Node<T>* root) {
                if (root->right == nullptr) {
                    return root->left;
                }

                root->right = DeleteMax(root->right);
                return Balance(root);
            }

            template <typename T>
            Node<T>* Delete(Node<T>* root, T data) {
                if (root == nullptr) {
                    return nullptr;
                }

                if (data < root->data) {
                    root->left = Delete(root->left, data);
                } else if (data > root->data) {
                    root->right = Delete(root->right, data);
                } else {
                    Node<T>* left = root->left;
                    Node<T>* right = root->right;
                    delete root;
                    if (left == nullptr) {
                        return right;
                    }
                    Node<T>* maximum = Maximum(left);
                    maximum->left = DeleteMax(left);
                    maximum->right = right;
                    return Balance(maximum);
                }
                return Balance(root);
            }
        """.trimIndent()
    }

    private fun getHeapTemplate(): String {
        return """
            template <typename T>
            struct Heap {
                T arr[1000001];
                int size = 0;
                bool is_max = false;
            };

            template <typename T>
            void SiftDown(Heap<T>& heap, int index) {
                int left = index * 2 + 1;
                int right = index * 2 + 2;
                int to_swap = index;

                if (heap.is_max) {
                    if (left < heap.size && heap.arr[left] > heap.arr[to_swap]) {
                        to_swap = left;
                    }

                    if (right < heap.size && heap.arr[right] > heap.arr[to_swap]) {
                        to_swap = right;
                    }
                } else {
                    if (left < heap.size && heap.arr[left] < heap.arr[to_swap]) {
                        to_swap = left;
                    }

                    if (right < heap.size && heap.arr[right] < heap.arr[to_swap]) {
                        to_swap = right;
                    }
                }

                if (to_swap == index) {
                    return;
                }

                T tmp = heap.arr[index];
                heap.arr[index] = heap.arr[to_swap];
                heap.arr[to_swap] = tmp;
                SiftDown(heap, to_swap);
            }

            template <typename T>
            void SiftUp(Heap<T>& heap, int index) {
                while (index > 0) {
                    bool condition;
                    if (heap.is_max) {
                        condition = heap.arr[index] > heap.arr[(index - 1) / 2];
                    } else {
                        condition = heap.arr[index] < heap.arr[(index - 1) / 2];
                    }

                    if (!condition) {
                        break;
                    }

                    T tmp = heap.arr[index];
                    heap.arr[index] = heap.arr[(index - 1) / 2];
                    heap.arr[(index - 1) / 2] = tmp;
                    index = (index - 1) / 2;
                }
            }

            template <typename T>
            void Insert(Heap<T>& heap, T data) {
                heap.arr[heap.size] = data;
                SiftUp(heap, heap.size);
                ++heap.size;
            }

            template <typename T>
            int Extract(Heap<T>& heap) {
                T to_return = heap.arr[0];
                heap.arr[0] = heap.arr[heap.size - 1];
                --heap.size;
                SiftDown(heap, 0);
                return to_return;
            }
        """.trimIndent()
    }
}
